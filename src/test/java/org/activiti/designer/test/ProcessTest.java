package org.activiti.designer.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.test.PvmTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class ProcessTest extends PvmTestCase {

	// 流程引擎初始化
	protected ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();
	
	/*********** 各类服务 ***********/
	protected RepositoryService repositoryService = processEngine.getRepositoryService();
	protected RuntimeService runtimeService = processEngine.getRuntimeService();
	protected TaskService taskService = processEngine.getTaskService();
	protected FormService formService = processEngine.getFormService();
	protected HistoryService historyService = processEngine.getHistoryService();
	protected IdentityService identityService = processEngine.getIdentityService();
	protected ManagementService managementService = processEngine.getManagementService();
	
	/**
	 * 用户权限初始化
	 */
	@Test
	public void testIdentity(){
		identityService.saveUser(identityService.newUser("fozzie"));
		identityService.saveUser(identityService.newUser("kermit"));

		identityService.saveGroup(identityService.newGroup("leave_user"));
		identityService.saveGroup(identityService.newGroup("check_user"));

		identityService.createMembership("fozzie", "leave_user");
		identityService.createMembership("kermit", "check_user");
	}
	
	@Test
	public void testIdentityDelete() {
		identityService.deleteUser("fozzie");
		identityService.deleteUser("kermit");
		identityService.deleteGroup("leave_group");
		identityService.deleteGroup("check_group");
	}
	
	/**
	 * 流程部署
	 */
	@Test
	public void testDeploy() {
		/*
		repositoryService.createDeployment()
			.name("deployName")// act_re_deployment.NAME
			.addClasspathResource("diagrams/leave.activiti.bpmn20.xml")
			.deploy();
		*/
		
		/**
		 * 部署图片时，命名必须为leave2.activiti.jpg或leave2.activiti.{processKey}.jpg
		 * act_re_procdef.DGRM_RESOURCE_NAME_
		 */
		String zip = "/diagrams/leave.zip";
		ZipInputStream inputStream = null;
		try {
			inputStream = new ZipInputStream(this.getClass().getResourceAsStream(zip));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		repositoryService.createDeployment()
			.name("请假流程定义部署")// act_re_deployment.NAME
			.addZipInputStream(inputStream)
			.deploy();
	}
	
	/**
	 * 启动流程
	 */
	@Test
	public void testStartProcess() {
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "Activiti2");
		
		Authentication.setAuthenticatedUserId("fozzie");// act_hi_procinst.START_USER_ID
		String startUserId = Authentication.getAuthenticatedUserId();
		System.out.println(startUserId);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave2", variableMap);
		
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());
	}
	
	/**
	 * 获取候选用户的任务列表
	 */
	@Test
	public void testCandidateUserTask(){
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("fozzie").list();
		for (Task task : tasks) {
			System.out.println(task.getId()+"\t"+task.getName()+"\t"+task.getAssignee());
		}
	}
	
	/**
	 * 候选用户签收任务
	 */
	@Test
	public void testCandidateUserClaimTask(){
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("fozzie").orderByTaskCreateTime().desc().list();
		Task leaveTask = tasks.get(0);
		
		taskService.claim(leaveTask.getId(), "fozzie");
	}
	
	/**
	 * 获取用户的任务列表
	 */
	@Test
	public void testUserTask(){
		List<Task> tasks = taskService.createTaskQuery().taskAssignee("fozzie").orderByTaskCreateTime().desc().list();
		for (Task task : tasks) {
			System.out.println(task.getId()+"\t"+task.getName()+"\t"+task.getAssignee());
		}
	}
	
	/**
	 * 用户执行并完成任务
	 */
	@Test
	public void testCompleteUserTask(){
		List<Task> tasks = taskService.createTaskQuery().taskAssignee("fozzie").orderByTaskCreateTime().desc().list();
		Task leaveTask = tasks.get(0);
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 3);
		variables.put("leave_user", leaveTask.getAssignee());
		
		taskService.complete(leaveTask.getId(), variables);
	}
	
	/**
	 * 查询历史TaskInstance
	 */
	@Test
	public void testHistoricTaskInstanceQuery(){
		/**
		 * 查询操作人为fozzie的历史Task
		 */
		List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
			.taskAssignee("fozzie")
			.orderByHistoricTaskInstanceEndTime()
			.desc()
			.list();
		
		showTaskInfo(hisTaskList);
		
		/**
		 * 查询流程定义为leave2的历史Task
		 */
		hisTaskList = historyService.createHistoricTaskInstanceQuery()
			.processDefinitionId("leave2:2")
			.orderByHistoricActivityInstanceStartTime()
			.asc()
			.list();
		
		showTaskInfo(hisTaskList);
	
		/**
		 * 查询流程实例为301的历史Task
		 */
		hisTaskList = historyService.createHistoricTaskInstanceQuery()
			.processInstanceId("301")
			.orderByHistoricActivityInstanceStartTime()
			.asc()
			.list();
		
		showTaskInfo(hisTaskList);
		
		/**
		 * 查询流程执行为405的历史Task
		 */
		hisTaskList = historyService.createHistoricTaskInstanceQuery()
			.executionId("405")
			.orderByHistoricActivityInstanceStartTime()
			.asc()
			.list();
		
		showTaskInfo(hisTaskList);
		
		/**
		 * 未完成的历史Task
		 */
		hisTaskList = historyService.createHistoricTaskInstanceQuery()
			.unfinished()
			// .finished()
			.orderByHistoricActivityInstanceStartTime()
			.asc()
			.list();
		
		showTaskInfo(hisTaskList);
	}

	private void showTaskInfo(List<HistoricTaskInstance> hisTaskList) {
		for (HistoricTaskInstance historicTaskInstance : hisTaskList) {
			System.out.println(historicTaskInstance.getId()+"\t"+historicTaskInstance.getName());
		}
		System.out.println("========================");
	}
	
	/**
	 * 查询历史ProcessInstance
	 * ?? 如何查询startUserId
	 */
	@Test
	public void testHistoricProcessInstanceQuery(){
		List<HistoricProcessInstance> hisProIntList = historyService.createHistoricProcessInstanceQuery()
			.orderByProcessInstanceEndTime()
			.desc()
			.list();
		
		for (HistoricProcessInstance historicProcessInstance : hisProIntList) {
			System.out.println(historicProcessInstance.getId()+"\t"+historicProcessInstance.getStartUserId());
		}
		
	}
	
	/**
	 * 查询历史HistoricActivityInstance
	 * ?? 如何获取Activity的xy坐标
	 */
	@Test
	public void testHistoricActivityInstanceQuery(){
		List<HistoricActivityInstance> hisActList = historyService.createHistoricActivityInstanceQuery()
			.list();
		
		for (HistoricActivityInstance historicActivityInstance : hisActList) {
			
			ActivityImpl activity = (ActivityImpl) historicActivityInstance;
			System.out.println(activity.getX());
			
			System.out.println(historicActivityInstance.getActivityId()
					+"\t"+historicActivityInstance.getActivityName()
					+"\t"+historicActivityInstance.getAssignee());
		}
		
	}
	
	/**
	 * 获取流程图
	 */
	@Test
	public void testImage(){
		 ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
         	.processDefinitionKey("leave2")
         	.latestVersion()
         	// .orderByProcessDefinitionVersion()
         	// .desc()
         	.singleResult();

		 ProcessDefinitionImpl pdImpl = (ProcessDefinitionImpl) processDefinition;
		 String processDefinitionId = pdImpl.getId();
		 
		 ReadOnlyProcessDefinition def = ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processDefinitionId);
		 ActivityImpl actImpl = (ActivityImpl) def.getInitial();
		 System.out.println(actImpl.getX()+"\t" +actImpl.getY());
		 
		 /**
		  * ?? ProcessDefinitionImpl.getInitial()为null
		  */
		 // ActivityImpl actImpl = pdImpl.getInitial();
		 // System.out.println(actImpl.getX());
		 
		 ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
		 	.executionId("101")
		 	.singleResult();
		 // actImpl = execution.getActivity();
		 // System.out.println(actImpl.getX());

		 ReadOnlyProcessDefinition rOprocessDefinition = ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processDefinitionId);
		 String diagramResourceName = rOprocessDefinition.getDiagramResourceName();

		 // String diagramResourceName = processDefinition.getDiagramResourceName();
		 // InputStream imageStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);
	}
}