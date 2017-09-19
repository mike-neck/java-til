package boilerplate.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.doma.jdbc.tx.TransactionManager;

import boilerplate.AppConfig;
import boilerplate.DbResource;
import boilerplate.entity.Employee;

import java.util.List;

public class EmployeeDaoTest {

    @Rule
    public final DbResource dbResource = new DbResource();

    private final EmployeeDao dao = new EmployeeDaoImpl();

    private TransactionManager transactionManager;

    @Test
    public void testSelectById() {
        transactionManager.required(() -> {
            Employee employee = dao.selectById(1);
            assertNotNull(employee);
            assertEquals("ALLEN", employee.name);
            assertEquals(Integer.valueOf(30), employee.age);
            assertEquals(Integer.valueOf(0), employee.version);
        });
    }

    @Test
    public void testSelectByAge() {
        transactionManager.required(() -> {
            final List<Employee> employees = dao.selectByAge(35);
            assertEquals(2, employees.size());
        });
    }

    @Before
    public void setup() {
        transactionManager = AppConfig.singleton().getTransactionManager();
    }

    @Test
    public void testInsert() {
        final Employee employee = new Employee("テスト社員", 28);

        transactionManager.required(() -> {
            dao.insert(employee);
            assertNotNull(employee.id);
        });

        transactionManager.required(() -> {
            final Employee byId = dao.selectById(employee.id);
            assertEquals(employee.name, byId.name);
            assertEquals(employee.age, byId.age);
            assertEquals(Integer.valueOf(1), byId.version);
        });
    }

    @Test
    public void testUpdate() {
        transactionManager.required(() -> {
            final Employee employee = dao.selectById(1);
            assumeThat(employee.name, is("ALLEN"));
            assumeThat(employee.age, is(30));
            assumeThat(employee.version, is(0));

            employee.age = 50;

            dao.update(employee);

            assertEquals(Integer.valueOf(1), employee.version);
        });

        transactionManager.required(() -> {
            final Employee employee = dao.selectById(1);
            assertEquals("ALLEN", employee.name);
            assertEquals(Integer.valueOf(50), employee.age);
            assertEquals(Integer.valueOf(1), employee.version);
        });
    }

    @Test
    public void testDelete() {
        transactionManager.required(() -> {
            final Employee employee = dao.selectById(1);
            final int count = dao.delete(employee);
            assertEquals(1, count);
        });

        transactionManager.required(() -> {
            final Employee employee = dao.selectById(1);
            assertNull(employee);
        });
    }
}
