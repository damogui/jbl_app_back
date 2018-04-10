package hmap.core.login.service;

import java.util.Map;

public interface ILoginService {
  public boolean authenticate(String username, String password, Map<String, String> params);
  public String getName();
}
