package resource.secImpl;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

/**
 * �Լ�ʵ�ֵĹ����û������࣬Ҳ����ֱ��ʹ�� FilterSecurityInterceptor
 *
 * AbstractSecurityInterceptor�����������ࣺ
 * FilterSecurityInterceptor��������FilterInvocation��ʵ�ֶ�URL��Դ�����ء�
 * MethodSecurityInterceptor��������MethodInvocation��ʵ�ֶԷ������õ����ء�
 * AspectJSecurityInterceptor��������JoinPoint����Ҫ�����ڶ����淽��(AOP)���õ����ء�
 *
 * ������ֱ��ʹ��ע���Action�����������أ������ڷ����ϼӣ�
 * @PreAuthorize("hasRole('ROLE_SUPER')")
 * <!-- �û��Ƿ�ӵ����������Դ��Ȩ�� -->
 */
@Service
public class MyAccessDecisionManager implements AccessDecisionManager {
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		if(configAttributes == null) {
			return;
		}
		/*���������Դӵ�е�Ȩ��(һ����Դ�Զ��Ȩ��)*/
		Iterator<ConfigAttribute> iterator = configAttributes.iterator();
		/*��ǰ�û�����ԴȨ��*/
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		/*���Ȩ�ޣ��������ֻҪ�û��߱�����һ���ͱ�ʾ����ͨ��*/
		ConfigAttribute configAttribute;
		String needPermission;
		while(iterator.hasNext()) {
			configAttribute = iterator.next();
			//����������Դ����Ҫ��Ȩ��
			needPermission = configAttribute.getAttribute();
			//�û���ӵ�е�Ȩ��authentication
			for(GrantedAuthority ga : authorities) {
				if(needPermission.equals(ga.getAuthority())) {
					return;
				}
			}
		}
		throw new AccessDeniedException(" û��Ȩ�޷��ʣ� ");
	}

	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	public boolean supports(Class<?> clazz) {
		return true;
	}
	
}