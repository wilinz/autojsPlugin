package jb.plugin.autojs.ui;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.intellij.openapi.application.ApplicationManager;
import icons.AutoJsIcons;
import jb.plugin.autojs.AutoJsServer;
import jb.plugin.autojs.Device;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Set;

public class ServerDialog extends JDialog implements ServerDialogListener, WindowListener {
    //全局服务
    private AutoJsServer applicationService = ApplicationManager.getApplication().getService(AutoJsServer.class);
    private JPanel contentPane;
    private JLabel qrCodeImageLabel;
    private JButton startServerBtn;
    private JButton stopServerBtn;
    private JPanel leftPanel;
    private JLabel serverStatusLab;
    private JScrollPane deviceScrollPanel;

    public ServerDialog() {
        serverStatusLab.setIcon(AutoJsIcons.StatusNo);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(startServerBtn);

        leftPanel.setPreferredSize(new Dimension(300, 300));
        setSize(600, 300);
        this.setLocationRelativeTo(null);
        this.addWindowListener(this);

        startServerBtn.addActionListener(e -> {
            applicationService.start();
        });
        stopServerBtn.addActionListener(e -> {
            applicationService.stop();
        });
    }

    @Override
    public void updateQrCode(String text) {
        if (text == null || text.equals("")) {
            qrCodeImageLabel.setIcon(null);
            return;
        }
        BufferedImage bufferedImage = QrCodeUtil.generate(text, 200, 200);
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        qrCodeImageLabel.setIcon(imageIcon);
        qrCodeImageLabel.updateUI();
    }

    @Override
    public void updateServerStatus(Boolean status) {
        if (!status) {
            updateQrCode("");
        }
        serverStatusLab.setText(status ? "已启动" : "未启动");
        serverStatusLab.setIcon(status ? AutoJsIcons.StatusOk : AutoJsIcons.StatusNo);
    }

    @Override
    public void updateDeviceList(Set<Device> devices) {
        System.out.println("连接设备成功");

        String[] cNames = {"设备", "ip", "操作"};
        Object[][] data = new Object[devices.size()][3];
        for (int i = 0; i < devices.size(); i++) {
            Device device = (Device) devices.toArray()[i];
            data[i][0] = device.getInfo().getDeviceName();
            data[i][1] = device.getInfo().getIp();
            data[i][2] = device.getId();
        }
//        data[devices.size()][0] = "iphone13";
//        data[devices.size()][1] = "192.168.1.1";
//        data[devices.size()][2] = 1;
//
//        data[devices.size() + 1][0] = "iphone14";
//        data[devices.size() + 1][1] = "192.168.1.1";
//        data[devices.size() + 1][2] = 2;

        JTable jt = new JTable(data, cNames);
        jt.setRowHeight(30);
        deviceScrollPanel.setViewportView(jt);
//        jt.setModel(new DefaultTableModel() {
//            @Override
//            public Object getValueAt(int row, int column) {
//                return data[row][column];
////                return (row + 1) * (column + 1);
//            }
//
//            @Override
//            public int getRowCount() {
//                return 3;
//            }
//
//            @Override
//            public int getColumnCount() {
//                return 3;
//            }
//
//            @Override
//            public void setValueAt(Object aValue, int row, int column) {
//                System.out.println(aValue + "  setValueAt");
//            }
//
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                // 带有按钮列的功能这里必须要返回true不然按钮点击时不会触发编辑效果，也就不会触发事件。
//                if (column == 2) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
        jt.getColumnModel().getColumn(2).setCellEditor(new MyButtonEditor());
        jt.getColumnModel().getColumn(2).setCellRenderer(new MyButtonRender());
        jt.setRowSelectionAllowed(false);// 禁止表格的选择功能。不然在点击按钮时表格的整行都会被选中。也可以通过其它方式来实现。
    }

    //        table1.removeAll();
//    Vector vData = new Vector();
//    Vector vName = new Vector();
//        vName.add("column1");
//        vName.add("column2");
//    Vector vRow = new Vector();
//        vRow.add("cell 0 0");
//        vRow.add("cell 0 1");
//        vData.add(vRow.clone());
//        vData.add(vRow.clone());
//    DefaultTableModel model = new DefaultTableModel(vData, vName);
//        table1.setModel(model);
//        table1.setRowSelectionAllowed(false);
//    //        table1.setCellSelectionEnabled(false);
////        table1.setEnabled(false);
//    TableColumn tableColumn1 = new TableColumn(0, 100);
//        tableColumn1.setHeaderValue("操作");
//        table1.addColumn(tableColumn1);
//        table1.getColumnModel().getColumn(2).setCellRenderer(new MyButtonRender());
//        table1.getColumnModel().getColumn(2).setCellEditor(new MyButtonEditor());
    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("windowOpened");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        this.updateServerStatus(applicationService.isRunning());
        this.updateQrCode(applicationService.getHostPort());
        this.updateDeviceList(applicationService.getDevices());

        applicationService.addServerDialogListener(this);
        System.out.println("windowActivated");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        applicationService.removeServerDialogListener();
        System.out.println("windowClosing");
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("windowClosed");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("windowIconified");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("windowDeiconified");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        System.out.println("windowDeactivated");
    }

    public class MyButtonRender implements TableCellRenderer {
        private JPanel panel;

        private JButton button;

        public MyButtonRender() {
            this.initButton();

            this.initPanel();

            // 添加按钮。
            this.panel.add(this.button);
        }

        private void initButton() {
            this.button = new JButton();

            // 设置按钮的大小及位置。
            this.button.setBounds(0, 0, 60, 25);

            // 在渲染器里边添加按钮的事件是不会触发的
            // this.button.addActionListener(new ActionListener()
            // {
            //
            // public void actionPerformed(ActionEvent e)
            // {
            // // TODO Auto-generated method stub
            // }
            // });

        }

        private void initPanel() {
            this.panel = new JPanel();

            // panel使用绝对定位，这样button就不会充满整个单元格。
            this.panel.setLayout(null);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                       int column) {
            // 只为按钮赋值即可。也可以作其它操作，如绘背景等。
//            this.button.setText(value == null ? "" : String.valueOf(value));
            this.button.setText("断开");
            return this.panel;
        }

    }


    public class MyButtonEditor extends DefaultCellEditor {

        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = -6546334664166791132L;

        private JPanel panel;

        private JButton button;

        public MyButtonEditor() {
            // DefautlCellEditor有此构造器，需要传入一个，但这个不会使用到，直接new一个即可。
            super(new JTextField());

            // 设置点击几次激活编辑。
            this.setClickCountToStart(1);

            this.initButton();

            this.initPanel();

            // 添加按钮。
            this.panel.add(this.button);
        }

        private void initButton() {
            this.button = new JButton();

            // 设置按钮的大小及位置。
            this.button.setBounds(0, 0, 60, 25);

            // 为按钮添加事件。这里只能添加ActionListner事件，Mouse事件无效。
            this.button.addActionListener(e -> {
                try {
                    int i = Integer.parseInt(e.getActionCommand());
                    Device device = applicationService.getDeviceById(i);
                    if (device == null) {
                        System.out.println("设备不存在" + i);
                    } else {
                        device.close4Java();
                    }
                } catch (Exception e1) {
                    System.out.println("数据转换出错e1 = " + e1);
                }


                System.out.println("点击了按钮" + e.getActionCommand());
                System.out.println(button.getText());
                MyButtonEditor.this.fireEditingCanceled();
            });

        }

        private void initPanel() {
            this.panel = new JPanel();

            // panel使用绝对定位，这样button就不会充满整个单元格。
            this.panel.setLayout(null);
        }


        /**
         * 这里重写父类的编辑方法，返回一个JPanel对象即可（也可以直接返回一个Button对象，但是那样会填充满整个单元格）
         */
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // 只为按钮赋值即可。也可以作其它操作。
            this.button.setText(value == null ? "" : String.valueOf(value));

            return this.panel;
        }

        /**
         * 重写编辑单元格时获取的值。如果不重写，这里可能会为按钮设置错误的值。
         */
        @Override
        public Object getCellEditorValue() {
            return this.button.getText();
        }
    }

}
