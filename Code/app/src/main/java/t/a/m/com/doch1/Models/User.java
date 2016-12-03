package t.a.m.com.doch1.Models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

/**
 * Created by Morad on 12/3/2016.
 */
@IgnoreExtraProperties
public class User {

    public static final String USERS_REFERENCE_KEY = "users";
    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String EMAIL_PROPERTY = "email";
    public static final String IS_MANAGER_PROPERTY = "isManager";

    @PropertyName(ID_PROPERTY)
    private String id;
    @PropertyName(NAME_PROPERTY)
    private String name;
    @PropertyName(EMAIL_PROPERTY)
    private String email;
    @PropertyName(IS_MANAGER_PROPERTY)
    public boolean isManager;

    public User(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
