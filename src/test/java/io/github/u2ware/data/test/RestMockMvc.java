package io.github.u2ware.data.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
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
	
//	protected static Log logger = LogFactory.getLog(RestMockMvc.class);

	private MockMvc mvc;
	private String baseUri;
	private MockMvcUriTemplateVariables variables;

	
	public RestMockMvc(WebApplicationContext context) {
		this(MockMvcBuilders.webAppContextSetup(context).build(), 
			context.getBean(RepositoryRestConfiguration.class));
	}
	public RestMockMvc(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
		this(MockMvcBuilders.webAppContextSetup(context)
				.apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
				//.uris().withScheme(scheme).withHost(host).withPort(port);
			).build(),
			context.getBean(RepositoryRestConfiguration.class));
	}
	
	public RestMockMvc(MockMvc mvc) {
		this(mvc,"");
	}
	public RestMockMvc(MockMvc mvc, RepositoryRestConfiguration config) {
		this(mvc, config.getBasePath().toString());
	}
	public RestMockMvc(MockMvc mvc, String baseUri) {
		this.mvc = mvc;
		this.baseUri = baseUri;
		this.variables = new MockMvcUriTemplateVariables();
	}
	
	public MockMvcUriTemplateVariables variables() {
		return variables;
	}

	
	//////////////////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////////////////

	public MockMvcRequestSupport GET(String uri) throws Exception{
		return get(variables.resolveUri(uri, baseUri));
	}
	public MockMvcRequestSupport POST(String uri) throws Exception {
		return post(variables.resolveUri(uri, baseUri));
	}
	public MockMvcRequestSupport PUT(String uri) throws Exception{
		return put(variables.resolveUri(uri, baseUri));
	}
	public MockMvcRequestSupport PATCH(String uri) throws Exception{
		return patch(variables.resolveUri(uri, baseUri));
	}
	public MockMvcRequestSupport DELETE(String uri) throws Exception{
		return delete(variables.resolveUri(uri, baseUri));
	}
	public MockMvcRequestSupport OPTIONS(String uri) throws Exception {
		return options(variables.resolveUri(uri, baseUri));
	}
	public MockMvcRequestSupport HEAD(String uri) throws Exception {
		return head(variables.resolveUri(uri, baseUri));
	}
	public MockMvcRequestSupport MULTIPART(String uri) throws Exception {
		return multipart(variables.resolveUri(uri, baseUri));
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
		
		public MockMvcRequestSupport H() throws Exception{
			builder.header("Accept", "application/json"); return this;
		}
		
		public MockMvcRequestSupport H(String key, Object value) throws Exception{
			builder.header(key,  variables.resolveUri(value.toString())); return this;
		}
		public MockMvcRequestSupport H(HttpHeaders headers) throws Exception{
			builder.headers(headers); return this;
		}
		public MockMvcRequestSupport P(String key, Object value) throws Exception{
			builder.param(key, variables.resolveUri(value.toString())); return this;
		}
		public MockMvcRequestSupport P(MultiValueMap<String,String> params) throws Exception{
			builder.params(params); return this;
		}
		
		public MockMvcRequestSupport C(String key, String value) throws Exception{
			content.put(key, value != null ? variables.resolveValue(value) : null); 
			return this;
		}
		public MockMvcRequestSupport C(String key, Object value) throws Exception{
			content.put(key, value); 
			return this;
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

		private String characterEncoding = "utf-8";
		private String contentType = MediaType.APPLICATION_JSON_VALUE;
		
		public MockMvcRequestSupport characterEncoding(String characterEncoding) throws Exception{
			this.characterEncoding = characterEncoding;
			return this;
		}
		public MockMvcRequestSupport contentType(String contentType) throws Exception{
			this.contentType = contentType;
			return this;
		}

		
		private ResultActionsSupport perform() throws Exception {
			
			if(content.size() > 0) {
				builder.characterEncoding(characterEncoding);
				builder.contentType(contentType);
				builder.content(mapper.writeValueAsString(content));
			}
			if(contentValue != null) {
				builder.characterEncoding(characterEncoding);
				String x = "{}";
				if(ClassUtils.isAssignableValue(MockMvc.class, contentValue)) {
					builder.contentType(contentType);
				}else {
//					System.err.println(contentType);
					
					builder.contentType(contentType);
					
					if(ClassUtils.isAssignableValue(String.class, contentValue)) {
						x = contentValue.toString();
					}else {
						x = mapper.writeValueAsString(contentValue);
					}
//					System.err.println(x);
				}
				builder.content(x);
			}
			return new ResultActionsSupport(mvc.perform(builder).andDo(MockMvcResultHandlers.print()), variables);
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
	}
	
	
	
	public static class ResultActionsSupport{
		
		private ResultActions actions;
		private MockMvcUriTemplateVariables variables;
		
		private ResultActionsSupport(ResultActions actions, MockMvcUriTemplateVariables variables) {
			this.actions = actions;
			this.variables = variables;
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
			
			if(value == null) {
				actions.andExpect(MockMvcResultMatchers.jsonPath(path).doesNotExist());
			}else {
				actions.andExpect(MockMvcResultMatchers.jsonPath(path).value(value));
			}
			return this;
		}
//		public ResultActionsSupport jsonExpect(int value) throws Exception {
//			actions.andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(value));
//			return this;
//		}
		public MvcResultSupport andReturn() throws Exception {
			return new MvcResultSupport(actions.andReturn());
		}
		public MvcResultSupport andReturn(String key) throws Exception {
			actions.andDo(variables.resultHandler(key));
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
		
		public <T> T contentAs(Class<T> type)  {
			try {
				return (T)new ObjectMapper().readValue(contentAsString(), type);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		public String contentAsString()  {
			try {
				return mvcResult.getResponse().getContentAsString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public <T> T path(String path) {
			try {
				String body = mvcResult.getResponse().getContentAsString();
				Object document = Configuration.defaultConfiguration().jsonProvider().parse(body);
				return JsonPath.read(document, path);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		public String link()  {
			String uri = null;
			uri = mvcResult.getResponse().getHeader("Location");
			if (uri != null) {
				return uri;
			}
			uri = (String) path("$._links.self.href");
			if (uri != null) {
				return uri;
			}
			uri = mvcResult.getRequest().getRequestURL().toString();
			if (uri != null) {
				return uri;
			}
			return null;
		}
	}
	
	
	@SuppressWarnings("serial")
	public static class MockMvcUriTemplateVariables extends HashMap<String, MvcResultSupport> implements UriTemplateVariables{

		private ResultHandler resultHandler(final String key) {
			return (mvcResult)->{
				put(key, new MvcResultSupport(mvcResult));
			};
		}
		
//		"{mtoLink1}" ==> link...
//		"{mtoLink1.$}" ==> body(map)
//		"{mtoLink1.$.prop}" ==> body path
		
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
					if(containsKey(key)) {
//						String jsonPath = "$"+name.substring(idx);
//						if("$.$".equals(jsonPath)) jsonPath = "$";
						String jsonPath = name.substring(idx+1);
						return get(key).path(jsonPath);
					}else {
						return null;
					}
				}
			}
		}
		
		public String resolveUri(String template) {
			return UriComponentsBuilder.fromUriString(template).build().expand(variables()).toUriString();
		}
		public String resolveUri(String template, String baseUri) throws Exception{
			String convert = resolveUri(template);
			return template.equals(convert) ? baseUri +template : convert;
		}
		
		public Object resolveValue(String template) {
			if(template.startsWith("{") && template.endsWith("}")) {
				String key = UriComponentsBuilder.fromUriString(template).build().expand( (name)->{ return name;}).toUriString();
				return getValue(key);
			}else {
				return resolveUri(template);
			}
		}

		private UriTemplateVariables variables() {
			return this;
		}
		
	}
	
	
//	
//	@SuppressWarnings("unchecked")
//	public abstract static class Docs<T> {
//
//		private final Class<?> INTERESTED_TYPE = resolveTypeArgument(getClass(), Docs.class);
//		protected ObjectMapper mapper = new ObjectMapper();; 
//		protected Map<String, Map<String,Object>> results = new HashMap<>(); 
//
//		protected abstract void random(T entity);
//		protected abstract void random(Map<String,Object> entity);
//		
//		public final T get() throws Exception{
//			Constructor<?> c = ClassUtils.getConstructorIfAvailable(INTERESTED_TYPE);
//			T entity = (T)c.newInstance();
//			random(entity);
//			return entity;
//		}
//		
//		public final Map<String,Object> get(String key) throws Exception{
//			Map<String,Object> entity = results.get(key);
//			random(entity);
//			return entity;
//		}
//		
//		public ResultHandler put(String key) {
//			return  (mvcResult ->{
//				try {
//					String src = mvcResult.getResponse().getContentAsString();
//					results.put(key, mapper.readValue(src, Map.class));
//				}catch(Exception e) {
//				}
//			});		
//		}
//	}
//	
//	public static class RestDocsBuilder {
//		
//		public static MockMvcRestDocumentationConfigurer configurer(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
//			MockMvcRestDocumentationConfigurer configurer = MockMvcRestDocumentation.documentationConfiguration(restDocumentation);
//
//			try {
//				String scheme = context.getBean(Environment.class).getProperty("spring.restdocs.scheme");
//				String host = context.getBean(Environment.class).getProperty("spring.restdocs.host");
//				int port = context.getBean(Environment.class).getProperty("spring.restdocs.port", Integer.class);
//				configurer.uris().withScheme(scheme).withHost(host).withPort(port);
//			}catch(Exception e) {
//			}
//			
//			return configurer;
//		}
//		
//		public interface Callback {
//			public void document(RestDocsBuilder builder);
//		}
//		
//		public static RestDocumentationResultHandler document(String identifier, Callback descriptor){
//			
//			RestDocsBuilder builder = new RestDocsBuilder(identifier);
//			
//			descriptor.document(builder);
//
//			List<Snippet> snippets = new ArrayList<>();
//			if(builder.requestParameters.size() >  0){snippets.add(RequestDocumentation.requestParameters(builder.requestParameters));}
//			if(builder.requestParts.size()      >  0){snippets.add(RequestDocumentation.requestParts(builder.requestParts));}
//			if(builder.requestHeaders.size()    > -1){snippets.add(HeaderDocumentation.requestHeaders(builder.requestHeaders));}
//			if(builder.requestFields.size()     >  0){snippets.add(PayloadDocumentation.requestFields(builder.requestFields));}
//			if(builder.responseHeaders.size()   > -1){snippets.add(HeaderDocumentation.responseHeaders(builder.responseHeaders));}
//			if(builder.responseFields.size()    >  0){snippets.add(PayloadDocumentation.responseFields(builder.responseFields));}
//			
//			return MockMvcRestDocumentation.document(identifier,
//					preprocessRequest(prettyPrint()),
//					preprocessResponse(prettyPrint()),
//					snippets.toArray(new Snippet[0])
//			);
//		}
//
//		private final ParameterDescriptors requestParameters = new ParameterDescriptors();
//		private final RequestPartDescriptors requestParts= new RequestPartDescriptors();
//		private final HeaderDescriptors requestHeaders = new HeaderDescriptors();
//		private final FieldDescriptors requestFields = new FieldDescriptors();
//		private final HeaderDescriptors responseHeaders = new HeaderDescriptors();
//		private final FieldDescriptors responseFields = new FieldDescriptors();
//		private final String identifier;
//
//		private RestDocsBuilder(String identifier) {
//			this.identifier = identifier;
//		}
//
//		public ParameterDescriptors requestParameters() {return requestParameters;}
//		public RequestPartDescriptors requestParts() {return requestParts;}
//		public HeaderDescriptors requestHeaders() {return requestHeaders;}
//		public FieldDescriptors requestFields() {return requestFields;}
//		public HeaderDescriptors responseHeaders() {return responseHeaders;}
//		public FieldDescriptors responseFields() {return responseFields;}
//		public String identifier() {return identifier;}
//		
//		@SuppressWarnings("serial")
//		public static class HeaderDescriptors extends ArrayList<HeaderDescriptor>{
//			public HeaderDescriptor headerWithName(String name){
//				HeaderDescriptor descriptor = HeaderDocumentation.headerWithName(name);
//				this.add(descriptor);
//				return descriptor;
//			}
//		}
//		
//		@SuppressWarnings("serial")
//		public static class FieldDescriptors extends ArrayList<FieldDescriptor>{
//			public FieldDescriptor fieldWithPath(String path){
//				FieldDescriptor descriptor = PayloadDocumentation.fieldWithPath(path);
//				this.add(descriptor);
//				return descriptor;
//			}
//
//			public FieldDescriptor subsectionWithPath(String path){
//				FieldDescriptor descriptor = PayloadDocumentation.subsectionWithPath(path);
//				this.add(descriptor);
//				return descriptor;
//			}
//		}
//		
//		@SuppressWarnings("serial")
//		public static class ParameterDescriptors extends ArrayList<ParameterDescriptor>{
//			public ParameterDescriptor parameterWithName(String name){
//				ParameterDescriptor descriptor = RequestDocumentation.parameterWithName(name);
//				this.add(descriptor);
//				return descriptor;
//			}
//		}
//		
//		@SuppressWarnings("serial")
//		public static class RequestPartDescriptors extends ArrayList<RequestPartDescriptor>{
//			public RequestPartDescriptor partWithName(String name){
//				RequestPartDescriptor descriptor = RequestDocumentation.partWithName(name);
//				this.add(descriptor);
//				return descriptor;
//			}
//		}
//	}
}
