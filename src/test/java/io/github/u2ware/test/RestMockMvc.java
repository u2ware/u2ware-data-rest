package io.github.u2ware.test;

import static org.springframework.core.GenericTypeResolver.resolveTypeArgument;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestPartDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents.UriTemplateVariables;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;


public class RestMockMvc {

	protected static void test(MockMvc mvc) throws Exception{
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(".....");
		ResultHandler handler = null;
		ResultMatcher matcher = null;
		ResultActions actions = mvc.perform(requestBuilder).andDo(handler).andExpect(matcher);
		MvcResult result = actions.andReturn();
		result.getResponse();
	}
	
	protected static Log logger = LogFactory.getLog(RestMockMvc.class);

	private MockMvc mvc;
	private String baseUri;
	private MockMvcUriTemplateVariables variables;

	public RestMockMvc(MockMvc mvc, String baseUri) {
		this.mvc = mvc;
		this.baseUri = baseUri;
		this.variables = new MockMvcUriTemplateVariables();
	}

	private UriTemplateVariables variables() {
		return variables;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////////////////
	public String getUrlTemplate(String uri) throws Exception{
		String convert = UriComponentsBuilder.fromUriString(uri).build().expand(variables()).toUriString();
		return uri.equals(convert) ? baseUri+uri : convert;
	}

	public MockMvcRequestSupport GET(String uri) throws Exception{
		return get(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport POST(String uri) throws Exception {
		return post(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport PUT(String uri) throws Exception{
		return put(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport PATCH(String uri) throws Exception{
		return patch(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport DELETE(String uri) throws Exception{
		return delete(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport OPTIONS(String uri) throws Exception {
		return options(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport HEAD(String uri) throws Exception {
		return head(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport MULTIPART(String uri) throws Exception {
		return multipart(getUrlTemplate(uri));
	}
	
	
	public MockMvcRequestSupport get(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.get(uri), variables, mvc);
	}
	public MockMvcRequestSupport post(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.post(uri), variables, mvc);
	}
	public MockMvcRequestSupport put(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.put(uri), variables, mvc);
	}
	public MockMvcRequestSupport patch(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.patch(uri), variables, mvc);
	}
	public MockMvcRequestSupport delete(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.delete(uri), variables, mvc);
	}
	public MockMvcRequestSupport options(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.options(uri), variables, mvc);
	}
	public MockMvcRequestSupport head(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.head(uri), variables, mvc);
	}
	public MockMvcRequestSupport multipart(String uri) throws Exception{
		System.out.println("\n-----------------------------------------------------------------------------------------------");
		return new MockMvcRequestSupport(MockMvcRequestBuilders.multipart(uri), variables, mvc);
	}
	
	
	
	
	@SuppressWarnings("serial")
	public static class MockMvcUriTemplateVariables extends HashMap<String, MvcResultSupport> implements UriTemplateVariables{

		@Override
		public Object getValue(String name) {

			if (containsKey(name)) {
				return get(name).link();
			}else {
				int idx = name.indexOf('.');
				if(idx < 0) {
					return null;
				}else {
					String key = name.substring(0, idx);
					String jsonPath = "$"+name.substring(idx);

					if(containsKey(key)) {
						return get(key).body(jsonPath);
					}else {
						return null;
					}
				}
			}
		}
		
		private ResultHandler resultHandler(final String key) {
			return (mvcResult)->{
				put(key, new MvcResultSupport(mvcResult));
			};
		}
	}
	
	
	
	public static class MockMvcRequestSupport{
		
		private MockHttpServletRequestBuilder builder;
		private MockMvcUriTemplateVariables variables;
		private MockMvc mvc;
		
		
		private ObjectMapper mapper = new ObjectMapper();
		private Map<String,Object> content = new HashMap<>();
		private Object contentValue;
		
		private MockMvcRequestSupport(MockHttpServletRequestBuilder builder, MockMvcUriTemplateVariables variables, MockMvc mvc) {
			this.builder = builder;
			this.variables = variables;
			this.mvc = mvc;
		}
		private MockMvcRequestSupport(MockMultipartHttpServletRequestBuilder builder, MockMvcUriTemplateVariables variables, MockMvc mvc) {
			this.builder = builder;
			this.variables = variables;
			this.mvc = mvc;
		}
		
		public UriTemplateVariables variables() {
			return variables;
		}

		
		public MockMvcRequestSupport H(String key, String value) throws Exception{
			builder.header(key, UriComponentsBuilder.fromUriString(value).build().expand(variables()).toUriString()); return this;
		}
		public MockMvcRequestSupport H(HttpHeaders headers) throws Exception{
			builder.headers(headers); return this;
		}
		public MockMvcRequestSupport P(String key, String value) throws Exception{
			builder.param(key, UriComponentsBuilder.fromUriString(value).build().expand(variables()).toUriString()); return this;
		}
		public MockMvcRequestSupport P(MultiValueMap<String,String> params) throws Exception{
			builder.params(params); return this;
		}
		public MockMvcRequestSupport C(String key, String value) throws Exception{
			content.put(key, UriComponentsBuilder.fromUriString(value).build().expand(variables()).toUriString()); return this;
		}
		public MockMvcRequestSupport C(String key, Object value) throws Exception{
			content.put(key, value); return this;
		}
		public MockMvcRequestSupport C(Object contentValue) throws Exception{
			this.contentValue = contentValue; return this;
		}
		public MockMvcRequestSupport C() throws Exception{
			this.contentValue = mvc; return this;
		}
		
		public MockMvcRequestSupport F(String key, File file) throws Exception{
			MockMultipartHttpServletRequestBuilder r = (MockMultipartHttpServletRequestBuilder)builder;
			r.file(new MockMultipartFile(key, FileCopyUtils.copyToByteArray(file)));
			return this;
		}
		public MockMvcRequestSupport F(MockMultipartFile file) throws Exception{
			MockMultipartHttpServletRequestBuilder r = (MockMultipartHttpServletRequestBuilder)builder;
			r.file(file);
			return this;
		}
		

		
		private ResultActionsSupport perform() throws Exception {
			
			if(content.size() > 0) {
				builder.contentType(MediaType.APPLICATION_JSON_UTF8);
				builder.content(mapper.writeValueAsString(content));
			}
			if(contentValue != null) {
				String x = "{}";
				if(ClassUtils.isAssignableValue(MockMvc.class, contentValue)) {
					builder.contentType(MediaType.APPLICATION_JSON_UTF8);
				}else {
					builder.contentType(MediaType.APPLICATION_JSON_UTF8);
					x = mapper.writeValueAsString(contentValue);
				}
				builder.content(x);
			}
			return new ResultActionsSupport(mvc.perform(builder).andDo(MockMvcResultHandlers.print()));
		}
		
		public ResultActionsSupport is2xx() throws Exception {
			return perform().andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		}
		public ResultActionsSupport is4xx() throws Exception {
			return perform().andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}
		public ResultActionsSupport is5xx() throws Exception {
			return perform().andExpect(MockMvcResultMatchers.status().is5xxServerError());
		}
		
		
		public ResultActionsSupport is2xx(String key) throws Exception {
			return is2xx().andDo(variables.resultHandler(key));
		}
		public ResultActionsSupport is4xx(String key) throws Exception {
			return is4xx().andDo(variables.resultHandler(key));
		}
		public ResultActionsSupport is5xx(String key) throws Exception {
			return is5xx().andDo(variables.resultHandler(key));
		}
	}
	
	
	
	public static class ResultActionsSupport{
		
		private ResultActions actions;
		
		private ResultActionsSupport(ResultActions actions) {
			this.actions = actions;
		}
		public ResultActionsSupport andDo(ResultHandler... resultHandlers) throws Exception{
			
			for(ResultHandler resultHandler : resultHandlers) {
				actions.andDo(resultHandler);
			}
			return this;
		}
		
		public ResultActionsSupport andExpect(ResultMatcher... resultMatchers) throws Exception{
			for(ResultMatcher resultMatcher : resultMatchers) {
				actions.andExpect(resultMatcher);
			}
			return this;
		}
		
		public ResultActionsSupport andExpect(String path, Object value) throws Exception {
			actions.andExpect(MockMvcResultMatchers.jsonPath(path).value(value));
			return this;
		}
		public ResultActionsSupport andExpect(int value) throws Exception {
			actions.andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(value));
			return this;
		}
		
		public MvcResultSupport andReturn() throws Exception {
			return new MvcResultSupport(actions.andReturn());
		}
	}
	
	
	
	public static class MvcResultSupport {
		
		private MvcResult mvcResult;

		private MvcResultSupport(MvcResult mvcResult) {
			this.mvcResult = mvcResult;
		}
		
		public MvcResult get() {
			return mvcResult;
		}
		
		public String link()  {
			String uri = null;
			uri = mvcResult.getResponse().getHeader("Location");
			if (uri != null) {
				return uri;
			}
			uri = (String) body("$._links.self.href");
			if (uri != null) {
				return uri;
			}
			uri = mvcResult.getRequest().getRequestURL().toString();
			if (uri != null) {
				return uri;
			}
			return null;
		}
		
		public String body()  {
			try {
				return mvcResult.getResponse().getContentAsString();
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		public <T> T body(String path) {
			try {
				String body = mvcResult.getResponse().getContentAsString();
				Object document = Configuration.defaultConfiguration().jsonProvider().parse(body);
				return JsonPath.read(document, path);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public abstract static class Docs<T> {

		private final Class<?> INTERESTED_TYPE = resolveTypeArgument(getClass(), Docs.class);
		protected ObjectMapper mapper = new ObjectMapper();; 
		protected Map<String, Map<String,Object>> results = new HashMap<>(); 

		protected abstract void random(T entity);
		protected abstract void random(Map<String,Object> entity);
		
		public final T get() throws Exception{
			Constructor<?> c = ClassUtils.getConstructorIfAvailable(INTERESTED_TYPE);
			T entity = (T)c.newInstance();
			random(entity);
			return entity;
		}
		
		public final Map<String,Object> get(String key) throws Exception{
			Map<String,Object> entity = results.get(key);
			random(entity);
			return entity;
		}
		
		public ResultHandler put(String key) {
			return  (mvcResult ->{
				try {
					String src = mvcResult.getResponse().getContentAsString();
					results.put(key, mapper.readValue(src, Map.class));
				}catch(Exception e) {
				}
			});		
		}
	}
	
	public static class DocsBuilder {
		
		public interface Callback {
			public void document(DocsBuilder builder);
		}
		
		public static RestDocumentationResultHandler document(String identifier, Callback descriptor){
			
			DocsBuilder builder = new DocsBuilder(identifier);
			
			descriptor.document(builder);

			List<Snippet> snippets = new ArrayList<>();
			if(builder.requestParameters.size() >  0){snippets.add(RequestDocumentation.requestParameters(builder.requestParameters));}
			if(builder.requestParts.size()      >  0){snippets.add(RequestDocumentation.requestParts(builder.requestParts));}
			if(builder.requestHeaders.size()    > -1){snippets.add(HeaderDocumentation.requestHeaders(builder.requestHeaders));}
			if(builder.requestFields.size()     >  0){snippets.add(PayloadDocumentation.requestFields(builder.requestFields));}
			if(builder.responseHeaders.size()   > -1){snippets.add(HeaderDocumentation.responseHeaders(builder.responseHeaders));}
			if(builder.responseFields.size()    >  0){snippets.add(PayloadDocumentation.responseFields(builder.responseFields));}
			
			return MockMvcRestDocumentation.document(identifier,
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					snippets.toArray(new Snippet[0])
			);
		}

		private final ParameterDescriptors requestParameters = new ParameterDescriptors();
		private final RequestPartDescriptors requestParts= new RequestPartDescriptors();
		private final HeaderDescriptors requestHeaders = new HeaderDescriptors();
		private final FieldDescriptors requestFields = new FieldDescriptors();
		private final HeaderDescriptors responseHeaders = new HeaderDescriptors();
		private final FieldDescriptors responseFields = new FieldDescriptors();
		private final String identifier;

		private DocsBuilder(String identifier) {
			this.identifier = identifier;
		}

		public ParameterDescriptors requestParameters() {return requestParameters;}
		public RequestPartDescriptors requestParts() {return requestParts;}
		public HeaderDescriptors requestHeaders() {return requestHeaders;}
		public FieldDescriptors requestFields() {return requestFields;}
		public HeaderDescriptors responseHeaders() {return responseHeaders;}
		public FieldDescriptors responseFields() {return responseFields;}
		public String identifier() {return identifier;}
		
		@SuppressWarnings("serial")
		public static class HeaderDescriptors extends ArrayList<HeaderDescriptor>{
			public HeaderDescriptor headerWithName(String name){
				HeaderDescriptor descriptor = HeaderDocumentation.headerWithName(name);
				this.add(descriptor);
				return descriptor;
			}
		}
		
		@SuppressWarnings("serial")
		public static class FieldDescriptors extends ArrayList<FieldDescriptor>{
			public FieldDescriptor fieldWithPath(String path){
				FieldDescriptor descriptor = PayloadDocumentation.fieldWithPath(path);
				this.add(descriptor);
				return descriptor;
			}

			public FieldDescriptor subsectionWithPath(String path){
				FieldDescriptor descriptor = PayloadDocumentation.subsectionWithPath(path);
				this.add(descriptor);
				return descriptor;
			}
		}
		
		@SuppressWarnings("serial")
		public static class ParameterDescriptors extends ArrayList<ParameterDescriptor>{
			public ParameterDescriptor parameterWithName(String name){
				ParameterDescriptor descriptor = RequestDocumentation.parameterWithName(name);
				this.add(descriptor);
				return descriptor;
			}
		}
		
		@SuppressWarnings("serial")
		public static class RequestPartDescriptors extends ArrayList<RequestPartDescriptor>{
			public RequestPartDescriptor partWithName(String name){
				RequestPartDescriptor descriptor = RequestDocumentation.partWithName(name);
				this.add(descriptor);
				return descriptor;
			}
		}
	}
}
