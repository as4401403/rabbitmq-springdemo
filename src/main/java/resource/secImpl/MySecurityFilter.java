package resource.secImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import java.io.IOException;

/**
 * ���ĵ�InterceptorStatusToken token = super.beforeInvocation(fi);
 * ��������Ƕ����accessDecisionManager:decide(Object object)
 *            ��securityMetadataSource:getAttributes(Object object)������
 * �Լ�ʵ�ֵĹ����û������࣬Ҳ����ֱ��ʹ�� FilterSecurityInterceptor
 *
 * AbstractSecurityInterceptor�����������ࣺ
 * FilterSecurityInterceptor��������FilterInvocation��ʵ�ֶ�URL��Դ�����ء�
 * MethodSecurityInterceptor��������MethodInvocation��ʵ�ֶԷ������õ����ء�
 * AspectJSecurityInterceptor��������JoinPoint����Ҫ�����ڶ����淽��(AOP)���õ����ء�
 *
 * ������ֱ��ʹ��ע���Action�����������أ������ڷ����ϼӣ�
 * @PreAuthorize("hasRole('ROLE_SUPER')")
 *
 */
@Service("mySecurityFilter")
public class MySecurityFilter extends AbstractSecurityInterceptor implements Filter {
	//��spring-security.xml���myFilter������securityMetadataSource��Ӧ��
	//����������������Ѿ���AbstractSecurityInterceptor����
	@Autowired
	private MySecurityMetadataSource securityMetadataSource;
	@Autowired
	private MyAccessDecisionManager accessDecisionManager;
	@Autowired
	private AuthenticationManager myAuthenticationManager; 
	
	@PostConstruct
	public void init(){
		super.setAuthenticationManager(myAuthenticationManager);
		super.setAccessDecisionManager(accessDecisionManager);
	}
	
	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		FilterInvocation fi = new FilterInvocation(request, response, chain);
		invoke(fi);
	}
	
	private void invoke(FilterInvocation fi) throws IOException, ServletException {
		InterceptorStatusToken token = super.beforeInvocation(fi);
		try {
			fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
		} finally {
			super.afterInvocation(token, null);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
	
	public void destroy() {
		
	}

	@Override
	public Class<? extends Object> getSecureObjectClass() {
		return FilterInvocation.class;
	}
}