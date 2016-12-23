package resource.secImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sys.dao.ResourcesMapper;
import sys.dao.UserMapper;
import sys.model.Resources;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * org.springframework.security.core.userdetails.User
 * 			����ʵ�� UserDetails �ӿڣ���������֤�ɹ���ᱻ�����ڵ�ǰ�ػ���principal������
 * ��ö���ķ�ʽ��
 * WebUserDetails webUserDetails = (WebUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
 * ����JSP�У�
 * <sec:authentication property="principal.username"/>
 *
 * �����Ҫ�����û����������ԣ�����ʵ�� UserDetails �ӿ���������Ӧ���Լ���
 * Ȩ����֤��
 */
@Service
public class MyUserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserMapper userDao;
	@Autowired
	private ResourcesMapper resourcesDao ;
	// ��¼�ɹ� �����û�����ԴȨ��
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// ȡ���û���Ȩ��
		sys.model.User users = userDao.querySingleUser(username);
		if  (users == null)
            throw new UsernameNotFoundException(username+" not exist!");
		Collection<GrantedAuthority> grantedAuths = obtionGrantedAuthorities(users);
		// ��װ��spring security��user
		User userDetail = new User(
				users.getUserName(),
				users.getUserPassword(),
				true, true, true, true,
				grantedAuths	//�û���Ȩ��
			);
		return userDetail;
	}

	// ȡ���û���Ȩ��
	private Set<GrantedAuthority> obtionGrantedAuthorities(sys.model.User user) {
		List<Resources> resources = resourcesDao.getUserResources(String.valueOf(user.getUserName()));
		Set<GrantedAuthority> authSet = new HashSet<GrantedAuthority>();
		for (Resources res : resources) {
			// ������˵�û���ӵ�е�Ȩ�ޣ� ע�⣺����"ROLE_"��ͷ
			authSet.add(new SimpleGrantedAuthority("ROLE_" + res.getResKey()));
		}
		return authSet;
	}
}