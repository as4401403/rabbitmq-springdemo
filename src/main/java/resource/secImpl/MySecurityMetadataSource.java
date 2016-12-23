package resource.secImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;
import sys.model.Resources;
import sys.dao.ResourcesMapper;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * ������Դ��Ȩ�޵Ķ�Ӧ��ϵ
 * */
@Service
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
	@Autowired
	private ResourcesMapper resourcesMapper;

	private static Map<String, Collection<ConfigAttribute>> resourceMap = null;

	public Collection<ConfigAttribute> getAllConfigAttributes() {

		return null;
	}

	public boolean supports(Class<?> clazz) {
		return true;
	}
	/**
	 * ����������Դ��Ȩ�޵Ĺ�ϵ
	 */
	@PostConstruct
	private void loadResourceDefine() {
//		System.err.println("-----------MySecurityMetadataSource loadResourceDefine ----------- ");
		if (resourceMap == null) {
			resourceMap = new HashMap<String, Collection<ConfigAttribute>>();
			List<Resources> resources = this.resourcesMapper.findAll();
			ConfigAttribute configAttribute;
			String resUrl;
			for (Resources resource : resources) {
				//ͨ����Դ��������ʾ�����Ȩ�� ע�⣺����"ROLE_"��ͷ
				configAttribute = new SecurityConfig("ROLE_" + resource.getResKey());
				resUrl = resource.getResUrl();
				if(resourceMap.containsKey(resUrl)){
					resourceMap.get(resUrl).add(configAttribute);
				}else {
                    //��put key-value,value list�����½�
					Collection<ConfigAttribute> configAttributes = new ArrayList<ConfigAttribute>();
					configAttributes.add(configAttribute);
					resourceMap.put(resource.getResUrl(), configAttributes);
				}
			}
		}
	}
	/**
	 * 	������������Դ����Ҫ��Ȩ��
	 */
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		String requestUrl = ((FilterInvocation) object).getRequestUrl();
		if(resourceMap == null) {
			loadResourceDefine();
		}
		if(requestUrl.indexOf("?")> -1){//���������ַ���������
			requestUrl=requestUrl.substring(0,requestUrl.indexOf("?"));
		}
		Collection<ConfigAttribute> configAttributes = resourceMap.get(requestUrl);
            /*���Ϊnull,��Ϊϵͳδ�������Դ·��*/
		if(configAttributes == null){
			configAttributes = resourceMap.get("undefine");//��Ȩ��ÿ���û���������,��δ�����url����ͨ��
		}
		return configAttributes;
	}
}