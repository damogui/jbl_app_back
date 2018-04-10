package hmap.core.login.service.impl;

import com.hand.hap.security.CustomUserDetails;
import com.hand.hap.security.PasswordManager;

import hmap.core.login.service.IClientDetailService;
import hmap.core.login.service.ILoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class LoginUserDetailServiceImpl implements IClientDetailService {
  private Logger logger = LoggerFactory.getLogger(LoginUserDetailServiceImpl.class);

  @Autowired
  private ILoginService loginService;

  @Resource(name = "passwordManager")
  PasswordManager passwordManager;

  @Override
  public UserDetails loadUserByUsername(String userName, String pwd)
      throws UsernameNotFoundException {
    logger.info("loadUserByUsername..........{}",userName);
    if (!loginService.authenticate(userName, pwd, null)) {
      return null;
    }

    Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

    /*
     * 客户化登录组件，在创建userdetail对象时将组件的名称附带上，这样就能够支持多种登录模式并存
     */
    //在token中不保存明文密码，使用密文

    UserDetails userDetails = new CustomUserDetails(1L, userName, passwordManager.encode(pwd), true,
        true, true, true, authorities);
    // ip解除限制
    logger.info("userDetails:" + userDetails.getAuthorities().size());
    return userDetails;
  }

  public ILoginService getLoginService() {
    return loginService;
  }

  public void setLoginService(ILoginService loginService) {
    this.loginService = loginService;
  }
}
