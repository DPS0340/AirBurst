/* Made by jiho lee, 2019 */
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AirBurst {
    private static final String ACCESS_TOKEN = "Token";

    JFrame frame = new JFrame("AirBurst");
    JButton upload = new JButton("upload");
    JButton cancel = new JButton("cancel");
    JPanel panel = new JPanel();
    JTextArea dropField = new JTextArea();
    List<String> filesToUpload = new ArrayList<String>();
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
    final DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

    public void createFrame() {
        dropField.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file: droppedFiles) {
                        filesToUpload.add(file.toString());
                        dropField.setText(dropField.getText() + file.toString() + "\n");
                        System.out.println(file.toString());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        ActionListener doUpload = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(String filestr: filesToUpload) {
                    File inputFile = new File(filestr);
                    try {
                        FileInputStream inputStream = new FileInputStream(inputFile);
                        try {
                            FileMetadata metadata = client.files().uploadBuilder("/" + inputFile.getName())
                                    .uploadAndFinish(inputStream);
                        } finally {
                            inputStream.close();
                            filesToUpload.clear();
                            dropField.setText("Upload Done!");
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
        };
        ActionListener doRemove = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dropField.setText("");
                filesToUpload.clear();
            }
        };
        upload.addActionListener(doUpload);
        cancel.addActionListener(doRemove);
        dropField.setPreferredSize(new Dimension(150, 300));
        frame.setSize(400, 400);
        frame.setLayout(new FlowLayout((FlowLayout.CENTER), 50, 50));
        upload.setPreferredSize(new Dimension(100, 100));
        cancel.setPreferredSize(new Dimension(100, 100));
        panel.add(dropField);
        panel.add(upload);
        panel.add(cancel);
        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) throws DbxException {
        AirBurst airBurst = new AirBurst();
        airBurst.createFrame();
    }
}