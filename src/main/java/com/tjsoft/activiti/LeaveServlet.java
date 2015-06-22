package com.tjsoft.activiti;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * Servlet implementation class LeaveServlet
 * Activiti5.2 跟踪流程图
 */
public class LeaveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String PROCESS_KEY = "process_leave";

	/**
     * Default constructor. 
     */
    public LeaveServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String command = request.getParameter("command");
		
		if(command.equals("listProcDef")){//流程列表，库中有哪些流程
			listProcDef(request, response);
		}
		if(command.equals("listProcInst")){//具体流程的任务列表
			listProcInst(request, response);
		}
		if(command.equals("listExecution")){//当前任务的执行情况
			listExecution(request, response);
		}
		if(command.equals("traceProcess")){//当前任务的流程图
			traceProcess(request, response);
		}
		if(command.equals("listTask")){
			listTask(request, response);
		}
		if(command.equals("operate")){//执行任务
			operateTask(request, response);
		}
		if(command.equals("deleteproc")){//删除发布的流程
			deleteProcDef(request, response);
		}
		if(command.equals("deploy")){//部署流程
			deployProcDef(request, response);
		}
		if(command.equals("start")){//启动流程
			startProcDef(request, response);
		}
		if(command.equals("identity")){//创建组织机构
			createIdentity(request, response);
		}
	}
	private void operateTask(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
		String operate = request.getParameter("operate");
		String day = request.getParameter("day");
		
		ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();
	
	    TaskService taskService = processEngine.getTaskService();
	    
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(user).orderByTaskCreateTime().desc().list();//用户所在组的任务
		
		for(Task task : tasks){
			taskService.claim(task.getId(), user);//认领任务，获得用户指定的任务			
		}
		if(user.equals("zhangsan")){//员工
			Map<String, Object> variables = new HashMap<String, Object>();
			tasks = taskService.createTaskQuery().taskAssignee(user).orderByTaskCreateTime().desc().list();
			for(Task task : tasks){
				variables.put("day", day);
				//variables.put("leave_user", task.getAssignee());
				//标识完成任务在该节点的操作
				taskService.complete(task.getId(), variables);//执行通过  有参
			}
		}
		if(user.equals("weiyi")){//部门经理
			tasks = taskService.createTaskQuery().taskAssignee(user).orderByTaskCreateTime().desc().list();
			for(Task task : tasks){				
				
				Map<String, Object> variables = new HashMap<String, Object>();
				if(operate.equals("1")){
//					variables.put("d", "2");
					variables.put("iscorrect", true);//通过
				}else if(operate.equals("0")){
					variables.put("iscorrect", false);//驳回
//					variables.put("d", "3");
				}
				taskService.complete(task.getId(),variables);//执行  有参
			}
		}
		if(user.equals("boss")){//老总
			tasks = taskService.createTaskQuery().taskAssignee(user).orderByTaskCreateTime().desc().list();
			for(Task task : tasks){
				//标识完成任务在该节点的操作
				Map<String, Object> variables = new HashMap<String, Object>();
				if(operate.equals("1")){
					variables.put("iscorrect", true);//通过
				}else if(operate.equals("0")){
					variables.put("iscorrect", false);//驳回
				}
				taskService.complete(task.getId(),variables);//执行  有参
			}
		}
//		tasks = taskService.createTaskQuery().taskAssignee(user).orderByTaskCreateTime().desc().list();//指定该用户执行的任务
//		for(Task task : tasks){
//			taskService.claim(task.getId(), user);//认领任务，获得用户指定的任务
//		}
		
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
	/**
	 * 任务列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listTask(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String execId = request.getParameter("execId");
		
		ProcessEngine processEngine = ProcessEngineConfiguration
			.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
			.buildProcessEngine();
		
		TaskService taskService = processEngine.getTaskService();
		
		List<Task> taskList = taskService.createTaskQuery()
			.executionId(execId)
			.list();
		
		request.setAttribute("taskList", taskList);
		request.getRequestDispatcher("task.jsp").forward(request, response);
	}
	
	/**
	 * 当前实例列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listExecution(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String processInstanceId = request.getParameter("procInstId");
		
		ProcessEngine processEngine = ProcessEngineConfiguration
			.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
			.buildProcessEngine();
		
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		List<Execution> exectionList = runtimeService.createExecutionQuery()
			.processInstanceId(processInstanceId)
			.list();
		
		request.setAttribute("exectionList", exectionList);
		request.getRequestDispatcher("execution.jsp").forward(request, response);
	}
	
	/**
	 * 流程实例列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listProcInst(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String processDefinitionId = request.getParameter("pdfid");
		
		ProcessEngine processEngine = ProcessEngineConfiguration
			.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
			.buildProcessEngine();
		
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		List<ProcessInstance> procInstList = runtimeService.createProcessInstanceQuery()
			.processDefinitionId(processDefinitionId)
			.list();
		
		request.setAttribute("procInstList", procInstList);
		request.getRequestDispatcher("procinst.jsp").forward(request, response);
	}

	/**
	 * 流程定义列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listProcDef(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ProcessEngine processEngine = ProcessEngineConfiguration
			.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
			.buildProcessEngine();

		RepositoryService repositoryService = processEngine.getRepositoryService();
		
		List<ProcessDefinition> procDefList = repositoryService.createProcessDefinitionQuery()
			.processDefinitionKey(PROCESS_KEY)//获得指定流程的工作流
			.orderByProcessDefinitionVersion()
			.desc()
			.list();
		
		request.setAttribute("procDefList", procDefList);
		request.getRequestDispatcher("procdef.jsp").forward(request, response);
	}
	/**
	 * 删除定义流程
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void deleteProcDef(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();

	RepositoryService repositoryService = processEngine.getRepositoryService();
	TaskService taskService = processEngine.getTaskService();
	List<ProcessDefinition> procDefList = repositoryService.createProcessDefinitionQuery()
		.processDefinitionKey(PROCESS_KEY)//获得指定流程的工作流
		.orderByProcessDefinitionVersion()
		.desc()
		.list();
		for(ProcessDefinition task : procDefList){
			System.out.println(task.getDeploymentId()+"    "+task.getId()+"         "+task.getKey()+"       "+task.getName());
			List<Task> tasks = taskService.createTaskQuery().processDefinitionId(task.getId()).list();
			for(Task t : tasks){
				taskService.deleteTask(t.getId());
			}			
			repositoryService.deleteDeployment(task.getDeploymentId());
		}
	    this.listProcDef(request, response);
	}
	/**
	 * 部署流程
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void deployProcDef(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 部署图片时，命名必须为leave2.activiti.jpg或leave2.activiti.{processKey}.jpg
		 * act_re_procdef.DGRM_RESOURCE_NAME_
		 */
		String zip = "/diagrams/leave.zip";
		//String zip = "E:\\mash5_information\\info\\activiti\\activiti_web22\\activiti_web\\src\\main\\resources\\diagrams\\leave.zip";
		ZipInputStream inputStream = null;
		try {
			inputStream = new ZipInputStream(this.getClass().getResourceAsStream(zip));			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();

	    RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment()
			.name("请假流程定义部署")// act_re_deployment.NAME
			.addZipInputStream(inputStream)
			.deploy();
		this.listProcDef(request, response);
	}
	/**
	 * 流程跟踪
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void traceProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String execId = request.getParameter("execId");
		
		ProcessEngine processEngine = ProcessEngineConfiguration
			.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
			.buildProcessEngine();

		RepositoryService repositoryService = processEngine.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
		 	.processDefinitionKey(PROCESS_KEY)
		 	.latestVersion()
		 	.singleResult();//最近版本的流程实例
		
		ProcessDefinitionImpl pdImpl = (ProcessDefinitionImpl) processDefinition;
		String processDefinitionId = pdImpl.getId();//流程标识
		 
		ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processDefinitionId);
		ActivityImpl actImpl = null;
		
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
		 	.executionId(execId)
		 	.singleResult();//执行实例
		 
		String activitiId = execution.getActivityId();//当前实例的执行到哪个节点
		 
		List<ActivityImpl> activitiList = def.getActivities();//获得当前任务的所有节点
		for (ActivityImpl activityImpl : activitiList) {
			String id = activityImpl.getId();
			if (id.equals(activitiId)) {//获得执行到那个节点
				actImpl = activityImpl;
				break;
			}
		}
		
		request.setAttribute("coordinateObj", actImpl);
		request.getRequestDispatcher("showImg.jsp").forward(request, response);
	}

	/**
	 * 启动流程
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void startProcDef(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("leave_day", "1");//流程名、编号
		
		ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, variableMap);
		
		System.out.println(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());
		this.listProcDef(request, response);
	}
	/**
	 * 创建组织机构
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void createIdentity(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();
		IdentityService identityService = processEngine.getIdentityService();
		
		identityService.saveUser(identityService.newUser("zhangsan"));//新建用户
		identityService.saveUser(identityService.newUser("weiyi"));
		identityService.saveUser(identityService.newUser("boss"));

		identityService.saveGroup(identityService.newGroup("leave_group"));//新建组
		identityService.saveGroup(identityService.newGroup("dept_check_group"));
		identityService.saveGroup(identityService.newGroup("boss_check_group"));

		identityService.createMembership("zhangsan", "leave_group");//加入组成员
		identityService.createMembership("weiyi", "dept_check_group");
		identityService.createMembership("boss", "boss_check_group");
		
		this.listProcDef(request, response);
	}
}
