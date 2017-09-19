package boilerplate.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import boilerplate.AppConfig;
import boilerplate.entity.Employee;

@Dao(config = AppConfig.class)
public interface EmployeeDao {

    @Select
    List<Employee> selectAll();

    @Select
    Employee selectById(Integer id);

    @Select
    List<Employee> selectByAge(final Integer age);

    @Insert
    int insert(Employee employee);

    @Update
    int update(Employee employee);

    @Delete
    int delete(Employee employee);

}
