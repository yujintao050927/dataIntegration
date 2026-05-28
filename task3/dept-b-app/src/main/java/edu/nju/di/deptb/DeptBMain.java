package edu.nju.di.deptb;

import edu.nju.di.deptb.dao.AccountDao;
import edu.nju.di.deptb.dao.ChoiceDao;
import edu.nju.di.deptb.dao.CourseDao;
import edu.nju.di.deptb.dao.StudentDao;

import javax.swing.SwingUtilities;

public final class DeptBMain {
    public static void main(String[] args) throws Exception {
        DeptBConfig config = DeptBConfig.load();

        DbProvider db = new DbProvider(config);
        StudentDao studentDao = new StudentDao(db);
        CourseDao courseDao = new CourseDao(db);
        ChoiceDao choiceDao = new ChoiceDao(db);
        AccountDao accountDao = new AccountDao(db);

        DeptBXmlService xmlService = new DeptBXmlService(studentDao, courseDao, choiceDao);
        DeptBHttpServer httpServer = new DeptBHttpServer(config.serverPort, xmlService, studentDao, courseDao, choiceDao);
        httpServer.start();

        System.out.println("[dept-b] listening on port " + config.serverPort);
        System.out.println("[dept-b] GET  http://localhost:" + config.serverPort + "/courses");
        System.out.println("[dept-b] GET  http://localhost:" + config.serverPort + "/students");
        System.out.println("[dept-b] GET  http://localhost:" + config.serverPort + "/choices");
        System.out.println("[dept-b] POST http://localhost:" + config.serverPort + "/crossSelect");

        SwingUtilities.invokeLater(() -> {
            DeptBGui gui = new DeptBGui(accountDao, studentDao, courseDao, choiceDao);
            gui.showLogin();
        });
    }
}
