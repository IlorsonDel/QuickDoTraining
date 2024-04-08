package ca.quickdo.module5;

import ca.quickdo.module5.services.PeopleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Module5Controller {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Random randomizer = new Random();
    private final List<Thread> threads = new ArrayList<>();

    @FXML
    private Button btnLaunchTask;

    @FXML
    private void initialize() {
        btnLaunchTask.setOnAction(this::onBtnLaunchTaskClicked_Executors);
    }

    public void shutdown() throws SQLException {
        executorService.shutdownNow();
        if(threads.isEmpty()) return;
        threads.forEach(Thread::stop);
        PeopleService.shutdown();
    }

    private void simulateSlowAction() {
        try {
            int count = 0;
            for (int i = 0; i < 2000000000; i++) {
                System.out.println(Thread.currentThread().getName() + " back online. Incrementing count from " + count + " to " + (count + 1));
                count += 1;
                var sleepTime = randomizer.nextInt(20000) + 1;
                System.out.println(Thread.currentThread().getName() + " incremented to " + (count + 1) + ". Thread sleeping for " + sleepTime / 1000 + " seconds");
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void onBtnLaunchTaskClicked_ThreadAPI(ActionEvent event) {
        final var thread = new Thread(this::simulateSlowAction, "Slow Thread");
        thread.start();
        threads.add(thread);
    }

    private void onBtnLaunchTaskClicked_Executors(ActionEvent event) {
        executorService.submit(this::simulateSlowAction);
    }
}