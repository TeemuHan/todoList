import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;


public class main extends Application {
    /**
     * Define doList-Object
     */
    public class doList {
        String task = "";
        String completed = "";

        doList() {}

        doList(String task, String completed) {
            this.task = task;
            this.completed = completed;
        }
        public void setTask(String task) {this.task = task;}
        public void setCompleted(String completed) {this.completed = task;}
        public String getTask() {return this.task;}
        public String getCompleted() {return this.completed;}

    }

    public static void main (String [] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /**
         * Defining program-window
         */
        primaryStage.setTitle("ToDoList");
        Pane root = new Pane();
        Scene scene = new Scene(root, 600, 800);
        /**
         * Creating buttons
         */
        Button btnAdd = new Button("Add");
        Button btnFinish = new Button("Ready");
        Button btnRemove = new Button("Delete");
        /**
         * Setting buttons layout
         */
        btnAdd.setLayoutX(500);
        btnAdd.setLayoutY(22);
        btnFinish.setLayoutX(500);
        btnFinish.setLayoutY(142);
        btnRemove.setLayoutX(500);
        btnRemove.setLayoutY(102);
        /**
         * Creating and defining tableview
         */
        TableView table = new TableView();
        table.setEditable(true);

        TableColumn task = new TableColumn("Task");
        TableColumn finished = new TableColumn("Completed");

        table.getColumns().addAll(task,finished);

        table.setPrefSize(450,800);
        table.resizeColumn(task,255);
        table.resizeColumn(finished,35);

        /**
         * Setting button and table-elements on root
         */
        root.getChildren().addAll(btnAdd,btnFinish,btnRemove,table);

        /**
         * Creating ArrayList
         */
        ObservableList<doList> listTask = FXCollections.observableArrayList();


        /**
         * Reading txt-file and setting data from txt-file to tableview
         */
        try (FileReader reader = new FileReader("list.txt");
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String taskName = line;
                String completed = br.readLine();
                listTask.add((new doList(taskName,completed)));
            }

            task.setCellValueFactory(new PropertyValueFactory<>("task"));
            finished.setCellValueFactory(new PropertyValueFactory<>("completed"));

            table.setItems(listTask);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Defining add-button actions
         */
        btnAdd.setOnAction(actionEvent -> {

            //Open and define new window
            Pane root1 = new Pane();
            Stage stage1 = new Stage();
            stage1.setTitle("New Task");
            stage1.setScene(new Scene(root1, 450, 200));

            Button btnAddList = new Button("Add");
            Button btnCancelList = new Button("Cancel");

            Text txt = new Text("Add task");
            TextField tasktxt = new TextField();

            btnAddList.setLayoutX(140);
            btnAddList.setLayoutY(140);
            btnCancelList.setLayoutX(250);
            btnCancelList.setLayoutY(140);

            /**
             * Defining add-button action
             */
            btnAddList.setOnAction(actionEvent2 -> {

                //Selecting file where to write
                try {
                    File file = new File("list.txt");
                    //Writing task in a textfile
                    if(file.exists()) {
                        FileWriter writer = new FileWriter(file,true);
                        BufferedWriter bw = new BufferedWriter(writer);
                        bw.write(tasktxt.getText());
                        bw.newLine();
                        bw.write("No");
                        bw.newLine();
                        bw.close();
                        tasktxt.clear();
                        //If file doesnt exist
                    } else {
                        file.createNewFile();
                        FileWriter writer = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(writer);
                        bw.write(tasktxt.getText());
                        bw.newLine();
                        bw.write("No");
                        bw.newLine();
                        bw.close();
                        tasktxt.clear();
                    }

                } catch (IOException e) {
                    e.printStackTrace();}
                /**
                 * Clears and refresh tasklist and tableview. Also closes window
                 */
                listTask.clear();
                try (FileReader reader = new FileReader("list.txt");
                     BufferedReader br = new BufferedReader(reader)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String taskName = line;
                        String completed = br.readLine();
                        listTask.add((new doList(taskName,completed)));
                    }

                    task.setCellValueFactory(new PropertyValueFactory<>("task"));
                    finished.setCellValueFactory(new PropertyValueFactory<>("completed"));

                    table.setItems(listTask);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stage1.close();

            });
            /**
             * Defining cancel-button
             * Only closes window
             */
            btnCancelList.setOnAction(actionEvent1 -> {
                stage1.close();
            });

            txt.setLayoutX(180);
            txt.setLayoutY(40);

            tasktxt.setLayoutY(60);
            tasktxt.setLayoutX(50);
            tasktxt.setPrefWidth(350);

            root1.getChildren().addAll(btnAddList,btnCancelList,tasktxt,txt);

            stage1.show();
        });

        /**
         * Defining Remove-button actions
         */
        btnRemove.setOnAction(actionEvent4 -> {
            //Constructors
            doList selected = (doList) table.getSelectionModel().getSelectedItem();
            String lineToRemove = selected.getTask();
            //Original file and temporary file
            File listFile = new File("list.txt");
            File tempFile = new File("tempFile.txt");

            try {BufferedReader reader = new BufferedReader(new FileReader(listFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String currentLine;
                //Reading original file and writing on a temporary file
                while((currentLine = reader.readLine()) != null) {
                    String trimmed = currentLine.trim();
                    //if currenline is same as line to remove loop skips it and not writing it in a temporary file
                    if(trimmed.equals(lineToRemove)) {
                        reader.readLine();
                        continue; }
                    //writes currentline in a temporary file
                    writer.write(currentLine);
                    writer.newLine();

                }

                writer.close();
                reader.close();

                //Deleting original file and rename temporary same as original was
                listFile.delete();
                tempFile.renameTo(listFile);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             * refreshing tasklist and tableview
             */
            listTask.clear();
            try (FileReader reader = new FileReader("list.txt");
                 BufferedReader br = new BufferedReader(reader)) {
                String readingLine;
                while ((readingLine = br.readLine()) != null) {
                    String taskName = readingLine;
                    String completed = br.readLine();
                    listTask.add((new doList(taskName,completed)));
                }

                task.setCellValueFactory(new PropertyValueFactory<>("task"));
                finished.setCellValueFactory(new PropertyValueFactory<>("completed"));

                table.setItems(listTask);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }});
        /**
         * Defining Finish-button action
         */
        btnFinish.setOnAction(actionEvent5 -> {
            //Constructors
            doList selected = (doList) table.getSelectionModel().getSelectedItem();
            String lineToChange = selected.getTask();
            //Original and temporary file
            File listFile = new File("list.txt");
            File tempFile = new File("tempFile.txt");

            try {BufferedReader reader = new BufferedReader(new FileReader(listFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String currentLine;

                //Searching from original file task what we selected and writing all in a temporary file.

                while((currentLine = reader.readLine()) != null) {
                    String trimmed = currentLine.trim();

                    //when task founded it writes a new status in a new file

                    if(trimmed.equals(lineToChange)) {
                        writer.write(currentLine);
                        reader.readLine();
                        writer.newLine();
                        writer.write("Yes");
                        writer.newLine();
                    } else{

                        writer.write(currentLine);
                        writer.newLine();}

                }

                writer.close();
                reader.close();
                listFile.delete();
                tempFile.renameTo(listFile);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             * Refreshing tasklist and tableview
             */
            listTask.clear();
            try (FileReader reader = new FileReader("list.txt");
                 BufferedReader br = new BufferedReader(reader)) {
                String readingLine;
                while ((readingLine = br.readLine()) != null) {
                    String taskName = readingLine;
                    String completed = br.readLine();
                    listTask.add((new doList(taskName,completed)));
                }

                task.setCellValueFactory(new PropertyValueFactory<>("task"));
                finished.setCellValueFactory(new PropertyValueFactory<>("completed"));

                table.setItems(listTask);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });


        primaryStage.setScene(scene);

        primaryStage.show();
    }
}