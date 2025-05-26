package goldstamp.two.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

public class MemberDto extends User {

    private String loginId, password, name;

    private boolean social;

    private List<String> roleNames = new ArrayList<>(); //권한 이름 목록

    public MemberDto(String loginId, String password, String name, Boolean social, List<String> roleNames) {
        super(
                loginId,
                password,
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));

        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.social = social;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("loginId", loginId);
        dataMap.put("password",password);
        dataMap.put("name", name);
        dataMap.put("social", social);
        dataMap.put("roleNames", roleNames);

        return dataMap;

    }
}
