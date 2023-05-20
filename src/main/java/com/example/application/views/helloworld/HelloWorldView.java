package com.example.application.views.helloworld;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.states.StateName;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.application.services.IlivalidatorService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import static org.awaitility.Awaitility.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HelloWorldView extends HorizontalLayout {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    StorageProvider storageProvider;

    @Autowired
    private IlivalidatorService ilivalidatorService;

    private JobId jobId = null;
    
    private TextField name;
    
    private Button sayHello;

    public Button getButton() {
        return sayHello;
    }
    
    public HelloWorldView() {
        name = new TextField("Your stupid name");
        sayHello = new Button("Say hello");
        
        
        
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        FileBuffer fileBuffer = new FileBuffer();
        FileSystemReceiver receiver = new FileSystemReceiver();
        
        Upload singleFileUpload = new Upload(receiver);
        
        int maxFileSizeInBytes = 30 * 1024 * 1024; // 30MB
        singleFileUpload.setMaxFileSize(maxFileSizeInBytes);

//        singleFileUpload.setMaxFiles(3);

//        singleFileUpload.addSucceededListener(new MySucceededEventListener(jobId));
//        UUID jobUuid = UUID.randomUUID();
//        JobId jobId = new JobId(jobUuid);
        singleFileUpload.addSucceededListener(event -> {
            // Get information about the file that was written to the file
            // system
//            FileData savedFileData = receiver.getFileData();
//            String absolutePath = savedFileData.getFile().getAbsolutePath();

            
            File file = new File(receiver.getFilename());

            System.out.printf(receiver.getFilename());
            System.out.printf("File saved to: %s%n", file.getAbsolutePath());

            jobId = jobScheduler.enqueue(() -> ilivalidatorService.validate(file.getAbsolutePath()));
            log.info(jobId.toString());

            // Hier sollte ich eine Job-ID zurückbekommen.
            // Mit dieser Frage ich im FinishedListener nach.
            
            
//            for (int i=0; i<20; i++) {
//                try {
//                    System.out.println("do something...");
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        });
        
        singleFileUpload.addFinishedListener(event -> {
            System.out.println(event.getMIMEType());
            System.out.println(jobId);
            
            await()
                .with().pollDelay(2, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
                .and()
                .with().atMost(1, TimeUnit.MINUTES)
                .until(new MyCallable(jobId));

            
        });

//        singleFileUpload.
        
        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello, singleFileUpload);

        add(name, sayHello, singleFileUpload);
    }
    
    public class MyCallable implements Callable<Boolean> {  
        private final JobId jobId;
           
        public MyCallable(JobId jobId) {
            this.jobId = jobId;
        }
       
        @Override
        public Boolean call() throws Exception {
            log.info("*******************************************************");
            log.info("polling: {}", jobId.asUUID().toString());
            log.info("*******************************************************");
            
            Job job = storageProvider.getJobById(jobId.asUUID());        
            return job.getJobState().getName().equals(StateName.SUCCEEDED) ? true : false;            
        }
    }
    
    private class FileSystemReceiver implements Receiver {
        private static final long serialVersionUID = 1L;

        public static String BASE_PATH = "/tmp/";
        private File file;
        private String filename;

        public OutputStream receiveUpload(String filename, String mimeType) {
            // Create upload stream
            FileOutputStream fos = null; // Stream to write to
            try {
                // Open the file for writing.
                this.filename = FileSystemReceiver.BASE_PATH + filename;

                file = new File(this.filename);
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                return null;
            }
            return fos; // Return the output stream to write to
        }

        public String getFilename() {
            return filename;
        }
    } 
    
//  private class MySucceededEventListener implements ComponentEventListener<SucceededEvent> {
//  JobId jobId;
//  
//  public MySucceededEventListener(JobId jobId) {
//      this.jobId = jobId;
//  }
//
//  @Override
//  public void onComponentEvent(SucceededEvent event) {
//      FileSystemReceiver receiver = (FileSystemReceiver) event.getUpload().getReceiver();
//      File file = new File(receiver.getFilename());
//
//      System.out.printf(receiver.getFilename());
//      System.out.printf("File saved to: %s%n", file.getAbsolutePath());
//
//      this.jobId = jobScheduler.enqueue(() -> ilivalidatorService.validate(file.getAbsolutePath()));
//      log.info(this.jobId.toString());            
//  }
//}

}
