package cn.qulei.oa.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * role
 * @author 
 */
public class Role implements Serializable {
    private Integer id;

    private String name;
    private List<Permission> perList;


    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Permission> getPerList() {
        return perList;
    }

    public void setPerList(List<Permission> perList) {
        this.perList = perList;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", perList=" + perList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) &&
                Objects.equals(name, role.name) &&
                Objects.equals(perList, role.perList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, perList);
    }
}