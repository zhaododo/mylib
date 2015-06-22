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
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class LeaveTest {

	private static final String PROCESS_KEY = "process_leave";
	
	// 流程引擎初始化
	protected ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();
	
	/*********** 各类服务 ***********/
	protected RepositoryService repositoryService = processEngine.getRepositoryService();//获得activiti服务
	protected RuntimeService runtimeService = processEngine.getRuntimeService();//用于管理运行时流程实例
	protected TaskService taskService = processEngine.getTaskService();//用于管理运行时任务
	protected FormService formService = processEngine.getFormService();//用于管理任务表单
	protected HistoryService historyService = processEngine.getHistoryService();//管理流程实例、任务实例等历史数据
	protected IdentityService identityService = processEngine.getIdentityService();//用于管理组织结构
	protected ManagementService managementService = processEngine.getManagementService();//用于管理定时任务
	

	
	/**
	 * 流程部署
	 */
	@Test
	public void testDeploy() {//发布项目
		
		
		/*通过api方式发布流程
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
//	@Test
//	public void testStartProcess() {
//		Map<String, Object> variableMap = new HashMap<String, Object>();
//		variableMap.put("leave_day", "1");//流程名、编号
//		
//		Authentication.setAuthenticatedUserId("zhangsan");// act_hi_procinst.START_USER_ID
//		String startUserId = Authentication.getAuthenticatedUserId();
//		System.out.println(startUserId);
//		
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, variableMap);
//		
//		System.out.println(processInstance.getId());//业务id
//		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());//流程id
//	}
	

	
	/**
	 * 执行
	 */
//	@Test
//	public void testCandidateUserClaimTask(){
//		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("zhangsan").orderByTaskCreateTime().desc().list();
//		Task leaveTask = tasks.get(0);
//		//认领任务，获得用户指定的任务
//		taskService.claim(leaveTask.getId(), "zhangsan");
//		
//		//获取用户的任务列表
//		 tasks = taskService.createTaskQuery().taskAssignee("zhangsan").orderByTaskCreateTime().desc().list();
//		for (Task task : tasks) {
//			System.out.println(task.getId()+"\t"+task.getName()+"\t"+task.getAssignee());
//		}
//		
//		//用户执行并完成任务
//		tasks = taskService.createTaskQuery().taskAssignee("zhangsan").orderByTaskCreateTime().desc().list();
//		 leaveTask = tasks.get(0);
//		 taskService.complete(leaveTask.getId());
//	}
	
	
	/**
	 * 
	 */
	@Test
	public void testCompleteUserTask(){
		
	
	}

}