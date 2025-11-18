package com.qdmh.studentsmartcardqdmh.client.app.util;

import com.qdmh.studentsmartcardqdmh.client.app.entities.Appointment;
import com.qdmh.studentsmartcardqdmh.client.app.entities.Transaction;
import com.qdmh.studentsmartcardqdmh.client.app.entities.Bill;
import com.qdmh.studentsmartcardqdmh.client.app.entities.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HibernateService {
    public HibernateService() {

    }


    public static List<Appointment> loadAppointmentData(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        List<Appointment> appointments = new ArrayList<>();
        try {
            Query<Appointment> query = session.createQuery("From Appointment where patientId = :id", Appointment.class);
            query.setParameter("id", id);
            appointments = query.list();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        session.close();
        return appointments;
    }

    public static List<Bill> loadBillDataForAdmin(String text, String fromDate, String toDate) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        List<Bill> bills = null;
        try {
            String hql = " FROM Bill b WHERE (b.appointmentCode like :text or b.code like :text or b.patientName like :text) ";
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                hql += " AND b.paymentDate between :fromDate AND :toDate ";
            }
            Query<Bill> query = session.createQuery(hql, Bill.class);
            query.setParameter("text", "%" + text + "%");
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
            }
            bills = query.list();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        session.close();
        return bills;
    }

    public static List<Bill> loadBillData(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        List<com.qdmh.studentsmartcardqdmh.client.app.entities.Bill> bills = null;
        try {
            Query<com.qdmh.studentsmartcardqdmh.client.app.entities.Bill> query = session.createQuery("From Bill where patientId = :id", com.qdmh.studentsmartcardqdmh.client.app.entities.Bill.class);
            query.setParameter("id", id);
            bills = query.list();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        session.close();
        return bills;
    }

    public static Boolean saveBillAndUpdateAppointment(Bill bill, List<Integer> ids) {
        String date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(new Date());
        bill.setPaymentDate(date);
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        int updatedRows = 0;
        try {
            session.persist(bill);
            session.flush();
            bill.generateBillCode();
            session.merge(bill);
            String hql = "UPDATE Appointment a SET a.status = true WHERE a.id in :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", ids);
            updatedRows = query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            updatedRows = 0;
            transaction.rollback();
            return false;
        }
        session.close();
        return updatedRows > 0;
    }


    public static Boolean saveAppointment(Appointment appointment) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        try {
            session.persist(appointment);
            session.flush();
            appointment.generateAppointmentCode();
            session.merge(appointment);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            return false;
        }
        session.close();
        return true;
    }

    public static int saveOrUpdateUser(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        int idCard = 0;
        try {
            Serializable id = session.save(user);
            idCard = (int) id;
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            idCard = 0;
        }
        session.close();
        return idCard;
    }
    //call chô nào
    public static byte[] getPublicKey(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        byte[] publicKey = null;
        try {
            Query<byte[]> query = session.createQuery(
                    "SELECT u.publicKey FROM User u WHERE u.id = :id", byte[].class
            );
            query.setParameter("id", id);
            // Fetch the result
            publicKey = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            publicKey = null;
            transaction.rollback();
        }
        session.close();
        return publicKey;
    }

    public static int savePublicKey(int id, byte[] publicKey) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        int updatedRows = 0;
        try {
            String hql = "UPDATE User u SET u.publicKey = :publicKey WHERE u.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("publicKey", publicKey);
            query.setParameter("id", id);
            updatedRows = query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            updatedRows = 0;
            transaction.rollback();
        }
        session.close();
        return updatedRows;
    }

    public static void saveBalanceHistory(Transaction balanceHistory) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        String date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(new Date());
        balanceHistory.setDate(date);
        try {
            session.save(balanceHistory);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        session.close();
    }


    public static List<Transaction> searchBalanceHistoryByPatientId(int patientId, String fromDate, String toDate, String type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = null;
        List<Transaction> transactionList = null;
        try {
            transaction = session.beginTransaction();
            String hql = "FROM Transaction bh WHERE bh.patientId = :patientId ";
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                hql += " AND bh.date between :fromDate AND :toDate ";
            }
            if (!type.isEmpty()) {
                hql += " AND bh.type = :type ";
            }
            Query<Transaction> query = session.createQuery(hql, Transaction.class);
            query.setParameter("patientId", patientId);
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
            }
            if (!type.isEmpty()) {
                query.setParameter("type", type);
            }
            transactionList = query.list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return transactionList;
    }



    public static List<Transaction> searchBalanceHistory(String name, String fromDate, String toDate) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = null;
        List<Transaction> transactionList = null;
        try {
            transaction = session.beginTransaction();
            String hql = "FROM Transaction bh WHERE bh.patientName like :name ";
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                hql += " AND bh.date between :fromDate AND :toDate ";
            }
            Query<Transaction> query = session.createQuery(hql, Transaction.class);
            query.setParameter("name", "%" + name + "%");
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
            }
            transactionList = query.list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return transactionList;
    }


    public static List<User> getUserList() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        List<User> userList = new ArrayList<>();
        try {
            Query<User> userQuery = session.createQuery("FROM User", User.class);
            userList = userQuery.getResultList();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return userList;
    }

    public static List<User> searchUsersByNameAndBHYT(String text) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        List<User> userList = new ArrayList<>();
        try {
            Query<User> userQuery = session.createQuery("FROM User where hoten like :text or mabn like :text", User.class);
            userQuery.setParameter("text", "%" + text + "%");
            userList = userQuery.getResultList();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return userList;
    }

    public static List<Appointment> searchAppointment(String name) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        List<Appointment> appointments = new ArrayList<>();
        try {
            Query<Appointment> query = session.createQuery("FROM Appointment where name like :name ", Appointment.class);
            query.setParameter("name", "%" + name + "%");
            appointments = query.list();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        return appointments;
    }

    public static Boolean updateBalance(int id, int cost) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        int updatedRows = 0;
        try {
            String hql = "UPDATE User u SET u.balance = :balance WHERE u.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("balance", cost);
            query.setParameter("id", id);
            updatedRows = query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        session.close();
        return updatedRows > 0;
    }

    public static Boolean updateInformation(int id, String hoTen, String ngaySinh, String maBenhNhan, String sdt, String gioiTinh, String queQuan) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        int updatedRows = 0;
        try {
            String hql = "UPDATE User u SET u.hoten = :name,u.quequan = :quequan,u.gioitinh = :gioitinh, u.mabn = :mabn, u.ngaysinh = :ngaySinh,u.sdt = :sdt WHERE u.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("name", hoTen);
            query.setParameter("ngaySinh", ngaySinh);
            query.setParameter("mabn", maBenhNhan);
            query.setParameter("sdt", sdt);
            query.setParameter("gioitinh", gioiTinh);
            query.setParameter("quequan", queQuan);
            query.setParameter("id", id);
            updatedRows = query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        session.close();
        return updatedRows > 0;
    }

    public static Boolean updatePinCode(int id, String pinCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        int updatedRows = 0;
        try {
            String hql = "UPDATE User u SET u.mapin = :pinCode WHERE u.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("pinCode", pinCode);
            query.setParameter("id", id);
            updatedRows = query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        session.close();
        return updatedRows > 0;
    }

    public static Boolean updateAppointmentInformation(int id, User user, String name, String date, String description, int cost) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        int updatedRows = 0;
        try {
            String hql = "UPDATE Appointment a SET a.name = :name,a.date = :date,a.patientId =:patientId,a.patientName=:patientName,a.description = :description, a.cost = :cost WHERE a.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("name", name);
            query.setParameter("date", date);
            query.setParameter("patientId", user.getId());
            query.setParameter("patientName", user.getHoten());
            query.setParameter("description", description);
            query.setParameter("cost", cost);
            query.setParameter("id", id);
            updatedRows = query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        session.close();
        return updatedRows > 0;
    }

    public static Boolean deleteAppointmentById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        int updatedRows = 0;
        try {
            Query query = session.createQuery("DELETE FROM Appointment WHERE id = :appointmentId");

            query.setParameter("appointmentId", id);
            updatedRows = query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        session.close();
        return updatedRows > 0;
    }

    public static Boolean deleteUserById(List<Integer> ids) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        int updatedRows = 0;
        try {
            Query query = session.createQuery("DELETE FROM User WHERE id in :id");
            query.setParameter("id", ids);
            updatedRows = query.executeUpdate();
            tx.commit();
        }catch (Exception e){
            tx.rollback();
            return false;
        }
        return true;
    }

    public static Boolean deleteBillByIds(List<Integer> ids) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        int updatedRows = 0;
        try {
            Query query = session.createQuery("DELETE FROM Bill WHERE id in :id");
            query.setParameter("id", ids);
            updatedRows = query.executeUpdate();
            tx.commit();
        }catch (Exception e){
            tx.rollback();
            return false;
        }
        return true;
    }


}
