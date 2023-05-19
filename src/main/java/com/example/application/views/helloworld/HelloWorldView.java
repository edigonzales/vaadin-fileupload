package com.example.application.views.helloworld;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HelloWorldView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public HelloWorldView() {
        name = new TextField("Your stupid name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        FileBuffer fileBuffer = new FileBuffer();
        Upload singleFileUpload = new Upload(fileBuffer);

        singleFileUpload.addSucceededListener(event -> {
            // Get information about the file that was written to the file
            // system
            FileData savedFileData = fileBuffer.getFileData();
            String absolutePath = savedFileData.getFile().getAbsolutePath();

            System.out.printf(savedFileData.getFileName());
            System.out.printf("File saved to: %s%n", absolutePath);
            
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
        });

        
        
        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello, singleFileUpload);

        add(name, sayHello, singleFileUpload);
    }

}
