package edu.nju.di.deptc;

import edu.nju.di.deptc.dao.AccountDao;
import edu.nju.di.deptc.dao.ChoiceDao;
import edu.nju.di.deptc.dao.CourseDao;
import edu.nju.di.deptc.dao.StudentDao;

import javax.swing.SwingUtilities;

public final class DeptCMain {
    public static void main(String[] args) throws Exception {
        DeptCConfig config = DeptCConfig.load();

        DbProvider db = new DbProvider(config);
        StudentDao studentDao = new StudentDao(db);
        CourseDao courseDao = new CourseDao(db);
        ChoiceDao choiceDao = new ChoiceDao(db);
        AccountDao accountDao = new AccountDao(db);

        DeptCXmlService xmlService = new DeptCXmlService(studentDao, courseDao, choiceDao);
        DeptCHttpServer httpServer = new DeptCHttpServer(config.serverPort, xmlService, studentDao, courseDao, choiceDao);
        httpServer.start();

        System.out.println("[dept-c] listening on port " + config.serverPort);
        System.out.println("[dept-c] GET  http://localhost:" + config.serverPort + "/courses");
        System.out.println("[dept-c] GET  http://localhost:" + config.serverPort + "/students");
        System.out.println("[dept-c] GET  http://localhost:" + config.serverPort + "/choices");
        System.out.println("[dept-c] POST http://localhost:" + config.serverPort + "/crossSelect");
        System.out.println("[dept-c] POST http://localhost:" + config.serverPort + "/crossDrop");

        SwingUtilities.invokeLater(() -> {
            DeptCGui gui = new DeptCGui(accountDao, studentDao, courseDao, choiceDao);
            gui.showLogin();
        });
    }
}
