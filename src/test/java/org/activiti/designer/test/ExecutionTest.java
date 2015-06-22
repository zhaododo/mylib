package org.activiti.designer.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.test.PvmTestCase;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

public class ExecutionTest extends PvmTestCase {

	// 流程引擎初始化
	protected ProcessEngine processEngine = ProcessEngineConfiguration
		.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
		.buildProcessEngine();
	
	/*********** 各类服务 ***********/
	protected RepositoryService repositoryService = processEngine.getRepositoryService();
	protected RuntimeService runtimeService = processEngine.getRuntimeService();
	
	@Test
	public void testQuery(){
		List<String> activityList = runtimeService.getActiveActivityIds("301");
		for (String activityId : activityList) {
			System.out.println(activityId);
		}
		
		List<ProcessInstance> procIntList = runtimeService.createProcessInstanceQuery()
			.orderByProcessDefinitionId()
			.asc()
			.list();
		
		for (ProcessInstance processInstance : procIntList) {
			System.out.println(processInstance.getId()+"\t"+
					processInstance.getProcessInstanceId()+"\t"+
					processInstance.getProcessDefinitionId());
		}
	}
	
	@Test
	public void testStartProcInst(){
		String processDefinitionId = "leave2:8";
		String businessKey = "leave2:8_201102250001";
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("startUser", "fozzie");
		variables.put("startDate", new Date());
		
		/**
		 * logined user: fozzie
		 */
		Authentication.setAuthenticatedUserId("fozzie");// act_hi_procinst.START_USER_ID
		runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
	}
	
	@Test
	public void testGetVariable(){
		String executionId = "1601";
		Map<String, Object> valMap = runtimeService.getVariables(executionId);
		System.out.println(valMap);
		
		valMap = runtimeService.getVariablesLocal(executionId);
		System.out.println(valMap);
	}
	
	@Test
	public void testSetVariable(){
		String executionId = "1601";
		runtimeService.setVariable(executionId, "mobile1", "ME525 DEF");
		runtimeService.setVariable(executionId, "mobile2", "MEIZU 9");
	}
}
