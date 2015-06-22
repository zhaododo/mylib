package org.activiti.designer.test;

import java.io.FileInputStream;
import java.util.List;
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
import org.activiti.engine.impl.test.PvmTestCase;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;

public class RepositoryTest extends PvmTestCase {

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
	
	@Test
	public void testDeployByZip() throws Exception{
		String barFileName = "D:/eclipse_workspace/activiti/src/main/resources/diagrams/leave2.zip"; 
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(barFileName)); 
		     
		repositoryService.createDeployment() 
		    .name("process-leave.zip") 
		    .addZipInputStream(inputStream) 
		    .deploy();
		
//		repositoryService.createDeployment()
//			.addClasspathResource(resource)
//			.
		
	}
	
	@Test
	public void testDeployByFileName(){
		
		repositoryService.createDeployment()
			.name("process-leave2.deploy")
			.addClasspathResource("diagrams/leave2.activiti.bpmn20.xml")
			.addClasspathResource("diagrams/leave2.activiti.jpg")
			// .addString("leave2.activiti.bpmn20.xml", "leave2流程定义")
			// .addString("leave2.activiti.png", "leave2流程图")
			.deploy();
		
	}

	@Test
	public void testDeleteDeploy(){
		/**
		 * delete: act_re_deployment, act_re_procdef
		 */
		/*
		repositoryService.deleteDeployment("1501");
		repositoryService.deleteDeployment("1501", true);
		
		List<Deployment> deployList = repositoryService.createDeploymentQuery()
			.orderByDeploymentId()
			.asc()
			.list();
		for (Deployment deployment : deployList) {
			// System.out.println(deployment.getId()+"\t"+deployment.getName());
		}
		
		List<ProcessDefinition> procDefList = repositoryService.createProcessDefinitionQuery()
			.orderByDeploymentId()
			.asc()
			.list();
		for (ProcessDefinition processDefinition : procDefList) {
			System.out.println(processDefinition.getDeploymentId()+"\t"+processDefinition.getId()+"\t"+
					processDefinition.getName());
		}
		*/
		repositoryService.deleteDeployment("1", true);
	}
	
	@Test
	public void testGetResources(){
		List<String> depRsList = repositoryService.getDeploymentResourceNames("1201");
		for (String resName : depRsList) {
			// System.out.println(resName);
		}
		
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery() 
        	.processDefinitionKey("leave2")
        	.latestVersion()
        	.singleResult(); 

		String diagramResourceName = processDefinition.getDiagramResourceName(); 
		
		Object image = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);
	}
}
