package org.acme.controller;

/*
 *        Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *       Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *        software and associated documentation files (the "Software"), to deal in the Software
 *        without restriction, including without limitation the rights to use, copy, modify,
 *        merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *        permit persons to whom the Software is furnished to do so.
 *
 *        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.acme.model.BedrockAPI;
import org.acme.service.BedrockAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Angel Conde
 *
 */
@RestController
@RequestMapping("/")
@Slf4j
public class BedrockController {
	private static String llm = "anthropic.claude-v2";
	private final BedrockAPIService bedrockService;


	@Autowired
	public BedrockController(BedrockAPIService bedrockService) {
		this.bedrockService = bedrockService;
	}

	@GetMapping("/llm")
	public String getLlm() {
		return "Current Model: " + llm;
	}

	@GetMapping("/llm/set")
	public String setLlm(@RequestParam(value = "model", required = true) String new_llm) {
		llm = new_llm;
		return "New model: " + llm;
	}

	@RequestMapping(method = RequestMethod.GET, value ="/bedrock")
	private String bedrockCall(@RequestParam(value = "prompt", required = true) String prompt, @RequestParam(value = "temperature", required=false, defaultValue = "0.8") String temperature){
		String res=bedrockService.queryBedrock(prompt,Float.parseFloat(temperature));
		log.info("inside CRUD controller uiCall " + new Date());
		return res;
	}



}