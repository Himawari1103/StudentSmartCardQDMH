package com.qdmh.studentsmartcardqdmh.client.app.view;

import com.qdmh.studentsmartcardqdmh.client.app.Card.Student;
import com.qdmh.studentsmartcardqdmh.client.app.Card.SmartCard;
import com.formdev.flatlaf.FlatLightLaf;
import com.qdmh.studentsmartcardqdmh.client.app.constant.Constant;
import com.qdmh.studentsmartcardqdmh.client.app.entities.Appointment;
import com.qdmh.studentsmartcardqdmh.client.app.entities.Transaction;
import com.qdmh.studentsmartcardqdmh.client.app.entities.Bill;
import com.qdmh.studentsmartcardqdmh.client.app.util.HibernateService;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static com.qdmh.studentsmartcardqdmh.client.app.util.Utils.getValueByRow;

/**
 * @author DELL
 */
public class PatientForm extends javax.swing.JFrame {
    List<Appointment> appointments = new ArrayList<>();
    List<Bill> bills = new ArrayList<>();
    private Appointment selectedAppointment;

    SmartCard card = SmartCard.getInstance();
    private DefaultTableModel tblModel1;
    private DefaultTableModel tblModel2;
    Student student = Student.getInstance();

    public PatientForm() {
        initComponents();
        init();
        initTable();
        loadAppointments();
        loadBillData();
    }

    public final void initTable() {
        tblModel1 = (DefaultTableModel) tblLichSu.getModel();
        String[] headerTblLichSu = new String[]{"STT", "Mã đơn khám", "Ngày khám", "Tên bệnh", "Chi phí", "Trạng thái", "Id", "Description"};
        tblModel1.setColumnIdentifiers(headerTblLichSu);
        tblLichSu.getColumnModel().getColumn(6).setMinWidth(0);
        tblLichSu.getColumnModel().getColumn(6).setMaxWidth(0);
        tblLichSu.getColumnModel().getColumn(6).setWidth(0);
        tblLichSu.getColumnModel().getColumn(7).setMinWidth(0);
        tblLichSu.getColumnModel().getColumn(7).setMaxWidth(0);
        tblLichSu.getColumnModel().getColumn(7).setWidth(0);

        tblModel2 = (DefaultTableModel) tblHoaDon.getModel();
        String[] headerTblHoaDon = new String[]{"STT", "Mã hóa đơn","Mã khám", "Ngày thanh toán", "Số tiền", "Id"};
        tblModel2.setColumnIdentifiers(headerTblHoaDon);
        tblHoaDon.getColumnModel().getColumn(5).setMinWidth(0); // Ẩn cột ID
        tblHoaDon.getColumnModel().getColumn(5).setMaxWidth(0); // Ẩn cột ID
        tblHoaDon.getColumnModel().getColumn(5).setWidth(0);
    }

    public void loadAppointments() {
        tblModel1.setRowCount(0); // Clear existing data
        int index = 1; // Auto-increment index starts at 1
        appointments = HibernateService.loadAppointmentData(student.getId());
        for (Appointment appointment : appointments) {
            tblModel1.addRow(new Object[]{
                    index++, // Auto-increment index
                    appointment.getCode(),
                    appointment.getDate(),
                    appointment.getName(),
                    appointment.getCost(),
                    appointment.getStatus() ? "Đã thanh toán" : "Chưa thanh toán",
                    appointment.getId(),
                    appointment.getDescription()
            });
        }
        selectedAppointment = new Appointment();
    }

    public void loadBillData() {
        tblModel2.setRowCount(0); // Clear existing data
        int index = 1; // Auto-increment index starts at 1
        bills = HibernateService.loadBillData(student.getId());
        for (Bill b : bills) {
            tblModel2.addRow(new Object[]{
                    index++, // Auto-increment index
                    b.getCode(),
                    b.getAppointmentCode(),
                    b.getPaymentDate(),
                    b.getCost(),
            });
        }
    }

    // General helper method to get the MedicalId of the selected row from any table
    public List<Appointment> getSelectedAppointment() {
        int[] selectedRows = tblLichSu.getSelectedRows();
        List<Appointment> appointments = new ArrayList<>();
        for (int row : selectedRows) {
            Appointment appointment = new Appointment();
            appointment.setCode(getValueByRow(tblLichSu, row, 1, String.class));
            appointment.setDate(getValueByRow(tblLichSu, row, 2, String.class));
            appointment.setName(getValueByRow(tblLichSu, row, 3, String.class));
            appointment.setCost(getValueByRow(tblLichSu, row, 4, Integer.class) == null ? 0 : getValueByRow(tblLichSu, row, 4, Integer.class));

            appointment.setStatus(!Objects.equals(getValueByRow(tblLichSu, row, 5, String.class), Constant.CHUA_THANH_TOAN));
            appointment.setId(getValueByRow(tblLichSu, row,6, Integer.class) == null ? 0 : getValueByRow(tblLichSu, row, 6, Integer.class));
            appointment.setDescription(getValueByRow(tblLichSu, row, 7,String.class));
            appointments.add(appointment);
        }
        return appointments;
    }

    public Boolean getAppointmentStatus(String status) {
        return Objects.equals(status, Constant.DA_THANH_TOAN);
    }


    private void init() {
        // Retrieve the Student instance
        // Set the values from the Student instance to the UI components
        jhoTen.setText(student.getHoten());
        jNgaySinh.setText(student.getNgaysinh());
        jQueQuan.setText(student.getQuequan());
        jGioiTinh.setText(student.getGioitinh());
        jSdt.setText(student.getSdt());
        jMaBenhNhan.setText(student.getMabn());
        jBalance.setText(String.valueOf(student.getBalance()));

        // Get and display the student picture
        BufferedImage picture = student.getPicture();
        if (picture != null) {
            Image scaledImage = picture.getScaledInstance(jPicture.getWidth(), jPicture.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon pictureIcon = new ImageIcon(scaledImage);
            jPicture.setIcon(pictureIcon);
        } else {
            System.out.println("No student picture available.");
        }
    }

    public void loadPatienInfo() {
        String[] patientInfo;
        patientInfo = card.getPatientInfo();
        BufferedImage patientPicture = card.GetPatientPicture();

        jhoTen.setText(patientInfo[0]);
        jNgaySinh.setText(patientInfo[1]);
        jQueQuan.setText(patientInfo[2]);
        jGioiTinh.setText(patientInfo[3]);
        jSdt.setText(patientInfo[4]);
        jMaBenhNhan.setText(patientInfo[5]);
        jBalance.setText(String.valueOf(student.getBalance()));
        // Get student picture
        if (patientPicture != null) {
            Image scaledImage = patientPicture.getScaledInstance(jPicture.getWidth(), jPicture.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon pictureIcon = new ImageIcon(scaledImage);
            jPicture.setIcon(pictureIcon);
        } else {
            System.out.println("Failed to retrieve student picture.");
        }
    }

    public void loadPatientBalance() {
        jBalance.setText(String.valueOf(student.getBalance()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        label6 = new java.awt.Label();
        label8 = new java.awt.Label();
        label11 = new java.awt.Label();
        label12 = new java.awt.Label();
        label3 = new java.awt.Label();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnChangeInfo = new javax.swing.JButton();
        btnChangePin = new javax.swing.JButton();
        jChargeCard = new javax.swing.JButton();
        jBalance = new javax.swing.JLabel();
        jhoTen = new javax.swing.JLabel();
        jNgaySinh = new javax.swing.JLabel();
        jQueQuan = new javax.swing.JLabel();
        jSdt = new javax.swing.JLabel();
        jGioiTinh = new javax.swing.JLabel();
        jMaBenhNhan = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        btnHistoryTopUp = new javax.swing.JButton();
        jPicture = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblLichSu = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblHoaDon = new javax.swing.JTable();
        jMakePayment = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jDisplayInfo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(151, 151, 151));

        jPanel2.setBackground(new java.awt.Color(238, 238, 238));

        label6.setText("Địa chỉ:");

        label8.setText("SÐT:");

        label11.setText("Giới tính:");

        label12.setText("BHYT:");

        label3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label3.setText("Số dư:");

        jLabel2.setText("Họ tên:");

        jLabel3.setText("Ngày sinh:");

        btnChangeInfo.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Yellow"));
        btnChangeInfo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnChangeInfo.setForeground(new java.awt.Color(255, 255, 255));
        btnChangeInfo.setText("Sửa thông tin");
        btnChangeInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeInfoActionPerformed(evt);
            }
        });

        btnChangePin.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Blue"));
        btnChangePin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnChangePin.setForeground(new java.awt.Color(255, 255, 255));
        btnChangePin.setText("Đổi mã pin");
        btnChangePin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangePinActionPerformed(evt);
            }
        });

        jChargeCard.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Green"));
        jChargeCard.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jChargeCard.setForeground(new java.awt.Color(255, 255, 255));
        jChargeCard.setText("Nạp tiền");
        jChargeCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChargeCardActionPerformed(evt);
            }
        });

        jBalance.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        jBalance.setText("0");

        jhoTen.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        jNgaySinh.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        jQueQuan.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        jSdt.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        jGioiTinh.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        jMaBenhNhan.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        jButton1.setBackground(new java.awt.Color(255, 51, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Đăng xuất");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnHistoryTopUp.setBackground(new java.awt.Color(255, 153, 153));
        btnHistoryTopUp.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHistoryTopUp.setForeground(new java.awt.Color(255, 255, 255));
        btnHistoryTopUp.setText("Lịch sử nạp");
        btnHistoryTopUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryTopUpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jQueQuan, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jhoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(3, 3, 3))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSdt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jMaBenhNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(0, 26, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnChangePin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHistoryTopUp, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jChargeCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChangeInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(46, 46, 46))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jhoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jQueQuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSdt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jGioiTinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label12, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMaBenhNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBalance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChangeInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHistoryTopUp, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChargeCard, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChangePin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        tblLichSu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblLichSu);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel5.setText("Lịch sử khám bệnh");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(355, 355, 355)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(394, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tblHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblHoaDon);

        jMakePayment.setBackground(new java.awt.Color(255, 0, 0));
        jMakePayment.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMakePayment.setForeground(new java.awt.Color(255, 255, 255));
        jMakePayment.setText("Thanh toán");
        jMakePayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMakePaymentActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel11.setText("Hóa đơn");

        jDisplayInfo.setBackground(new java.awt.Color(0, 153, 153));
        jDisplayInfo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jDisplayInfo.setForeground(new java.awt.Color(255, 255, 255));
        jDisplayInfo.setText("Xem đơn thuốc");
        jDisplayInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDisplayInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(384, 384, 384)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jDisplayInfo)
                .addGap(81, 81, 81)
                .addComponent(jMakePayment, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(129, 129, 129))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jMakePayment, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jDisplayInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChangeInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeInfoActionPerformed
        ChangeInfomationForm a = new ChangeInfomationForm(this, rootPaneCheckingEnabled);
        a.setVisible(true);
    }//GEN-LAST:event_btnChangeInfoActionPerformed

    private void btnChangePinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangePinActionPerformed
        ChangePin a = new ChangePin(this, rootPaneCheckingEnabled);
        a.setVisible(true);
    }//GEN-LAST:event_btnChangePinActionPerformed

    private void jChargeCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChargeCardActionPerformed
        InputMoney a = new InputMoney(this, rootPaneCheckingEnabled);
        a.setVisible(true);
    }//GEN-LAST:event_jChargeCardActionPerformed

    private void btnHistoryTopUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoryTopUpActionPerformed
        TopUpListPatientForm a = new TopUpListPatientForm(this, rootPaneCheckingEnabled);
        a.setVisible(true);
    }//GEN-LAST:event_btnHistoryTopUpActionPerformed

    private void jDisplayInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDisplayInfoActionPerformed
        // Determine the currently focused table
        if (tblLichSu.getSelectedRow() != -1) {
            // Fetch the MedicalId from tblLichSu
            Appointment appointment = getSelectedAppointment().get(0);

            if (appointment != null) {
                String status = appointment.getStatus() ? Constant.DA_THANH_TOAN : Constant.CHUA_THANH_TOAN;
                JOptionPane.showMessageDialog(this,
                        "Mã đơn khám: " + appointment.getCode() + "\n" +
                                "Tên bệnh: " + appointment.getName() + "\n" +
                                "Ngày khám: " + appointment.getDate() + "\n" +
                                "Mô tả: " + appointment.getDescription() + "\n" +
                                "Trạng thái: " + status,
                        "Thông tin Đơn Khám",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            // No table row selected
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một dòng trong bảng để xem thông tin.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }//GEN-LAST:event_jDisplayInfoActionPerformed




    private void jMakePaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMakePaymentActionPerformed
        // Check if a row is selected in tblLichSu
        if (tblLichSu.getSelectedRow() != -1) {
            List<Appointment> appointmentList = this.getSelectedAppointment();
            for (Appointment appointment : appointmentList) {
                if (appointment.getStatus()) {
                    JOptionPane.showMessageDialog(this,
                            "Đơn khám đã được thanh toán hoặc không thể thanh toán.",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }             // Prompt user to confirm payment
            if (appointmentList.size() == 1) {
                Appointment appointment = appointmentList.get(0);
                int response = JOptionPane.showConfirmDialog(this,
                        "Thanh toán đơn khám cho mã đơn: " + appointment.getCode(),
                        "Xác nhận thanh toán",
                        JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    // Check if the student's balance is sufficient
                    if (student.getBalance() < appointment.getCost()) {
                        JOptionPane.showMessageDialog(this,
                                "Số dư hiện tại của bạn không đủ",
                                "Thông báo",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Hiển thị dialog xác nhận với trường nhập mã PIN
                        JPasswordField pinField = new JPasswordField();
                        Object[] message = {
                                "Nhập mã PIN để xác nhận thanh toán:", pinField
                        };

                        int option = JOptionPane.showConfirmDialog(
                                this,
                                message,
                                "Xác nhận thanh toán",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (option == JOptionPane.OK_OPTION) {
                            // Lấy mã PIN từ người dùng
                            String enteredPin = new String(pinField.getPassword());
                            byte[] PublicKey = HibernateService.getPublicKey(Integer.parseInt(student.getCardId()));
                            if (card.VerifyCard(PublicKey)) {
                                if (card.VerifyPin(enteredPin)) {
                                    // Deduct the cost from the student's balance
                                    student.setBalance(-appointment.getCost());
                                    card.updatePatientBalance(String.valueOf(student.getBalance()));
                                    loadPatienInfo();

                                    // Update the status in the table to "Đã thanh toán"
                                    if (HibernateService.updateBalance(student.getId(), student.getBalance())) {
                                        Bill bill = new Bill();
                                        bill.setAppointmentId(appointment.getId());
                                        bill.setPatientId(student.getId());
                                        bill.setAppointmentCode(appointment.getCode());
                                        bill.setCost(appointment.getCost());
                                        bill.setPatientName(student.getHoten());
                                        HibernateService.saveBillAndUpdateAppointment(bill, List.of(appointment.getId()));
                                        JOptionPane.showMessageDialog(this,
                                                "Thanh toán thành công cho mã đơn: " + appointment.getCode(),
                                                "Thông báo",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        this.loadAppointments();
                                        this.loadBillData();
                                        // balance his
                                        Transaction transaction = new Transaction();
                                        transaction.setType(Constant.THANH_TOAN);
                                        transaction.setCost(bill.getCost());
                                        transaction.setPatientName(student.getHoten());
                                        transaction.setPatientId(student.getId());
                                        HibernateService.saveBalanceHistory(transaction);
                                    }
                                } else {
                                    // Mã PIN không chính xác
                                    JOptionPane.showMessageDialog(this,
                                            "Mã PIN không chính xác. Vui lòng thử lại.",
                                            "Lỗi",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                // Thẻ không được xác minh
                                JOptionPane.showMessageDialog(this,
                                        "Xác minh thẻ thất bại. Vui lòng kiểm tra thẻ và thử lại.",
                                        "Lỗi",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Bạn đã hủy xác nhận thanh toán.",
                                    "Thông báo",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }
                    }
                }
            } else {
                String codes = appointmentList.stream().map(Appointment::getCode).collect(Collectors.joining(", "));
                int cost = appointmentList.stream().mapToInt(Appointment::getCost).sum();
                List<Integer> appointmentIds = appointmentList.stream().map(Appointment::getId).collect(Collectors.toList());
                int response = JOptionPane.showConfirmDialog(this,
                        "Thanh toán đơn khám cho danh sách mã đơn: " + codes,
                        "Xác nhận thanh toán",
                        JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    // Check if the student's balance is sufficient
                    if (student.getBalance() < cost) {
                        JOptionPane.showMessageDialog(this,
                                "Số dư hiện tại của bạn không đủ",
                                "Thông báo",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        // Hiển thị dialog xác nhận với trường nhập mã PIN
                        JPasswordField pinField = new JPasswordField();
                        Object[] message = {
                                "Nhập mã PIN để xác nhận thanh toán:", pinField
                        };

                        int option = JOptionPane.showConfirmDialog(
                                this,
                                message,
                                "Xác nhận thanh toán",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (option == JOptionPane.OK_OPTION) {
                            // Lấy mã PIN từ người dùng
                            String enteredPin = new String(pinField.getPassword());
                            byte[] PublicKey = HibernateService.getPublicKey(Integer.parseInt(student.getCardId()));
                            if (card.VerifyCard(PublicKey)) {
                                if (card.VerifyPin(enteredPin)) {
                                    // Deduct the cost from the student's balance
                                    student.setBalance(-cost);
                                    card.updatePatientBalance(String.valueOf(student.getBalance()));
                                    loadPatienInfo();
                                    // Update the status in the table to "Đã thanh toán"
                                    // Update
                                    if (HibernateService.updateBalance(student.getId(), student.getBalance())) {
                                        Bill bill = new Bill();
                                        bill.setAppointmentId(null);
                                        bill.setPatientId(student.getId());
                                        bill.setAppointmentCode(codes);
                                        bill.setCost(cost);
                                        bill.setPaymentDate(student.getHoten());
                                        HibernateService.saveBillAndUpdateAppointment(bill,appointmentIds);
                                        JOptionPane.showMessageDialog(this,
                                                "Thanh toán thành công cho mã đơn: " + codes,
                                                "Thông báo",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        this.loadAppointments();
                                        this.loadBillData();
                                    }
                                } else {
                                    // Mã PIN không chính xác
                                    JOptionPane.showMessageDialog(this,
                                            "Mã PIN không chính xác. Vui lòng thử lại.",
                                            "Lỗi",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                // Thẻ không được xác minh
                                JOptionPane.showMessageDialog(this,
                                        "Xác minh thẻ thất bại. Vui lòng kiểm tra thẻ và thử lại.",
                                        "Lỗi",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Bạn đã hủy xác nhận thanh toán.",
                                    "Thông báo",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }

                    }
                }

            }

        } else {
            // No row is selected in tblLichSu
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một dòng trong bảng lịch sử để thực hiện thanh toán.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jMakePaymentActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int cf = JOptionPane.showConfirmDialog(
            null,
            "Bạn muốn đăng xuất?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            this.dispose();
            formLogin a = new formLogin();
            a.setVisible(true);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PatientForm().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeInfo;
    private javax.swing.JButton btnChangePin;
    private javax.swing.JButton btnHistoryTopUp;
    private javax.swing.JLabel jBalance;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jChargeCard;
    private javax.swing.JButton jDisplayInfo;
    private javax.swing.JLabel jGioiTinh;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jMaBenhNhan;
    private javax.swing.JButton jMakePayment;
    private javax.swing.JLabel jNgaySinh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel jPicture;
    private javax.swing.JLabel jQueQuan;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel jSdt;
    private javax.swing.JLabel jhoTen;
    private java.awt.Label label11;
    private java.awt.Label label12;
    private java.awt.Label label3;
    private java.awt.Label label6;
    private java.awt.Label label8;
    private javax.swing.JTable tblHoaDon;
    private javax.swing.JTable tblLichSu;
    // End of variables declaration//GEN-END:variables
}
