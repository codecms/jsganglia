package com.jsganglia.server.conf;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jsganglia.server.UdpServer.MessageProc;

@Controller
public class RuleController {

	
	@Autowired
	ConfigureLoad configureLoad;
	
	@ResponseBody
	@RequestMapping("/list/dropmessage")
	public Set<String> excludedmessage(){
		return MessageProc.excludeMessage;
	}
	
	@ResponseBody
	@RequestMapping("/list/rules")
	public Set<String> excluderules(){
		return configureLoad.getIncludeRule();
	}
	
	
	@ResponseBody
	@RequestMapping("/list/addrules/{rulestr}")
	public Set<String> addRules(@PathVariable("rulestr") String rulestr){
		Set<String> oldRules=configureLoad.getIncludeRule();
		oldRules.add(rulestr);
		configureLoad.WriteConf(oldRules);
		
		MessageProc.setIncludeRule(oldRules);
		MessageProc.excludeMessage.remove(rulestr);
		
		return oldRules;
	}
	
}
