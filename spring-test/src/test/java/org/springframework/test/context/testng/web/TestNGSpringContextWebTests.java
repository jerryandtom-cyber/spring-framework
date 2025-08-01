/*
 * Copyright 2002-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.testng.web;

import java.io.File;

import jakarta.servlet.ServletContext;
import org.testng.annotations.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestNG-based integration tests that verify support for loading a
 * {@link WebApplicationContext} when extending {@link AbstractTestNGSpringContextTests}.
 *
 * @author Sam Brannen
 * @since 3.2.7
 */
@ContextConfiguration
@WebAppConfiguration
class TestNGSpringContextWebTests extends AbstractTestNGSpringContextTests implements ServletContextAware {

	@Configuration
	static class Config {

		@Bean
		String foo() {
			return "enigma";
		}
	}


	ServletContext servletContext;

	@Autowired
	WebApplicationContext wac;

	@Autowired
	MockServletContext mockServletContext;

	@Autowired
	MockHttpServletRequest request;

	@Autowired
	MockHttpServletResponse response;

	@Autowired
	MockHttpSession session;

	@Autowired
	ServletWebRequest webRequest;

	@Autowired
	String foo;


	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Test
	void basicWacFeatures() throws Exception {
		assertThat(wac.getServletContext()).as("ServletContext should be set in the WAC.").isNotNull();

		assertThat(servletContext).as("ServletContext should have been set via ServletContextAware.").isNotNull();

		assertThat(mockServletContext).as("ServletContext should have been autowired from the WAC.").isNotNull();
		assertThat(request).as("MockHttpServletRequest should have been autowired from the WAC.").isNotNull();
		assertThat(response).as("MockHttpServletResponse should have been autowired from the WAC.").isNotNull();
		assertThat(session).as("MockHttpSession should have been autowired from the WAC.").isNotNull();
		assertThat(webRequest).as("ServletWebRequest should have been autowired from the WAC.").isNotNull();

		Object rootWac = mockServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		assertThat(rootWac)
			.as("Root WAC must be stored in the ServletContext as: " + WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)
			.isNotNull();
		assertThat(rootWac).as("test WAC and Root WAC in ServletContext must be the same object.").isSameAs(wac);
		assertThat(wac.getServletContext()).as("ServletContext instances must be the same object.").isSameAs(mockServletContext);
		assertThat(request.getServletContext()).as("ServletContext in the WAC and in the mock request").isSameAs(mockServletContext);

		assertThat(mockServletContext.getRealPath("index.jsp"))
			.as("Getting real path for ServletContext resource.")
			.isEqualTo(new File("src/main/webapp/index.jsp").getCanonicalPath());
	}

	@Test
	void fooEnigmaAutowired() {
		assertThat(foo).isEqualTo("enigma");
	}

}
