package io.github.u2ware.data.test;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestPartDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.ResultHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class RestMockMvcDocs {
	
//	private final Class<?> INTERESTED_TYPE = resolveTypeArgument(getClass(), RestDocs.class);
	protected ObjectMapper mapper = new ObjectMapper();
//	protected T instance ;
	protected Map<String,Object> random ;
	
//	public RestDocs() {
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//	}
	
	
//	protected abstract void random(T entity);
//	protected abstract void randomFrom(T entity);

	protected abstract void random(Map<String,Object> entity);
	protected abstract void randomFrom(Map<String,Object> entity);
	
	
	public final Map<String,Object> random() throws Exception{
		if(this.random == null) {
//			Constructor<?> c = ClassUtils.getConstructorIfAvailable(INTERESTED_TYPE);
//			this.instance = (T)c.newInstance();
//			random(this.instance);
			this.random = new HashMap<String,Object>();
			random(this.random);
		}else {
			randomFrom(this.random);
		}
		return this.random;
	}
	
	@SuppressWarnings("unchecked")
	public final ResultHandler randomTo() {
		return  (mvcResult ->{
			try {
				String src = mvcResult.getResponse().getContentAsString();
//				this.instance = (T)mapper.readValue(src, INTERESTED_TYPE);
				this.random = mapper.readValue(src, Map.class);
			}catch(Exception e) {
				e.printStackTrace();
			}
		});	
	}
	
	protected final ResultHandler document(String identifier, DescriptorsCallback callback){
		Descriptors descriptors = new Descriptors(identifier);
		
		callback.doWith(descriptors);

		List<Snippet> snippets = new ArrayList<>();
		if(descriptors.requestParameters.size() >  0){snippets.add(RequestDocumentation.requestParameters(descriptors.requestParameters));}
		if(descriptors.requestParts.size()      >  0){snippets.add(RequestDocumentation.requestParts(descriptors.requestParts));}
		if(descriptors.requestHeaders.size()    > -1){snippets.add(HeaderDocumentation.requestHeaders(descriptors.requestHeaders));}
		if(descriptors.requestFields.size()     >  0){snippets.add(PayloadDocumentation.requestFields(descriptors.requestFields));}
		if(descriptors.responseHeaders.size()   > -1){snippets.add(HeaderDocumentation.responseHeaders(descriptors.responseHeaders));}
		if(descriptors.responseFields.size()    >  0){snippets.add(PayloadDocumentation.responseFields(descriptors.responseFields));}
		
		return MockMvcRestDocumentation.document(identifier,
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				snippets.toArray(new Snippet[0])
		);
	}
	
	protected interface DescriptorsCallback {
		public void doWith(Descriptors descriptors);
	}
	
	protected static class Descriptors{
		
		private final ParameterDescriptors requestParameters = new ParameterDescriptors();
		private final RequestPartDescriptors requestParts= new RequestPartDescriptors();
		private final HeaderDescriptors requestHeaders = new HeaderDescriptors();
		private final FieldDescriptors requestFields = new FieldDescriptors();
		private final HeaderDescriptors responseHeaders = new HeaderDescriptors();
		private final FieldDescriptors responseFields = new FieldDescriptors();
		private final String identifier;

		private Descriptors(String identifier) {
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
