package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.entities.Hall;
import il.cshaifasweng.OCSFMediatorExample.entities.TheaterMovie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieShow;
import il.cshaifasweng.OCSFMediatorExample.entities.Theater;
import il.cshaifasweng.OCSFMediatorExample.entities.TheaterMovie;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.msgObject;

public class SimpleServer extends AbstractServer {
    private static Session session;

    public SimpleServer(int port) {
        super(port);

    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        msgObject msgObj = (msgObject) msg;
        String msgString = msgObj.getMsg();
        try {
            if (msgString.startsWith("#get")) get(msgObj, client);
            if (msgString.startsWith("#update")
                    || msgString.startsWith("#add")
                    || msgString.startsWith("#remove"))
                update(msgObj, client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get(msgObject msgobject, ConnectionToClient client) throws Exception {
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();

        String msgString = msgobject.getMsg();
        if (msgString.equals("#getAllMovies")) {
            try {
                client.sendToClient(getAllMovies());
                System.out.format("Sent movies to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msgString.equals("#getAllHalls")) {
            try {
                client.sendToClient(getAllHalls());
                System.out.format("Sent movies to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msgString.equals("#getAllTheatres")) {
            try {
                client.sendToClient(getAllTheatres());
                System.out.format("Sent movies to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msgString.equals("#getShows")) {
            int id = (int) msgobject.getObject();
            client.sendToClient(getMovieShowsbyid(id));
            System.out.format("Sent movies Show of movies id" + id + " to client %s\n", client.getInetAddress().getHostAddress());

        }
    }

    private void update(msgObject msgObj, ConnectionToClient client) {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            change(msgObj, client);
            session.flush();
            session.getTransaction().commit(); // Save everything.
        } catch (Exception exception) {
            if (session != null)
                session.getTransaction().rollback();
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null)
                session.close();
        }
    }


    private void change(msgObject msgObj, ConnectionToClient client) throws IOException {
        msgObject msg = new msgObject("", null);
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
        if (msgObj.getMsg().equals("#addMovie")) {
            session.save((TheaterMovie) msgObj.getObject());
            msg.setMsg("MovieAdded");
        } else if (msgObj.getMsg().equals("#updateMovie")) {
            session.update((TheaterMovie) msgObj.getObject());
            msg.setMsg("MovieUpdated");
        } else if (msgObj.getMsg().equals("#removeMovie")) {
            session.delete(((TheaterMovie) msgObj.getObject()));
            msg.setMsg("MovieRemoved");
        } else if (msgObj.getMsg().equals("#addMovieShow")) {
            session.save(((MovieShow) msgObj.getObject()));
            msg.setMsg("MovieShowAdded");
        } else if (msgObj.getMsg().equals("#updateMovieShow")) {
            msg.setMsg("MovieShowUpdated");
            session.update(((MovieShow) msgObj.getObject()));
        } else if (msgObj.getMsg().equals("#deleteMovieShow")) {
            session.delete(((MovieShow) msgObj.getObject()));
            msg.setMsg("MovieShowRemoved");
        }
        client.sendToClient(msg);
    }

    private static msgObject getAllMovies() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TheaterMovie> query = builder.createQuery(TheaterMovie.class);
        query.from(TheaterMovie.class);
        List<TheaterMovie> list = session.createQuery(query).getResultList();
        for (TheaterMovie m : list) {
            System.out.println(m.getDescription());
        }
        msgObject msg = new msgObject("AllMovies", list);
        return msg;
    }

    private static msgObject getAllHalls() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Hall> query = builder.createQuery(Hall.class);
        query.from(Hall.class);
        List<Hall> data = session.createQuery(query).getResultList();
        msgObject msg = new msgObject("AllHalls", data);
        return msg;
    }

    private static msgObject getAllTheatres() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Theater> query = builder.createQuery(Theater.class);
        query.from(Theater.class);
        List<Theater> data = session.createQuery(query).getResultList();
        msgObject msg = new msgObject("AllTheatres", data);
        return msg;
    }

    private static msgObject getMovieShowsbyid(int id) throws Exception {
        System.out.println("getting movie shows");
        //String sqlquery="SELECT * FROM demo.movieshow where movieid_id="+id;
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<MovieShow> query = builder.createQuery(MovieShow.class);
        query.from(MovieShow.class);
        List<MovieShow> data = session.createQuery(query).getResultList();
        System.out.println("before for");
        List<MovieShow> wantedList = new ArrayList();
        for (MovieShow ms : data) {
            System.out.println(ms.getBeginTime());
            if (ms.getMovieShowId() == id)
                wantedList.add(ms);
        }
        System.out.println("after for");
        System.out.println("the Arraylist size is:" + wantedList.size());
        msgObject msg = new msgObject("movieShowsForMovie", wantedList);
        return msg;
    }

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        // Add ALL of your entities here. You can also try adding a whole package.
        configuration.addAnnotatedClass(Hall.class);
        configuration.addAnnotatedClass(Theater.class);
        configuration.addAnnotatedClass(MovieShow.class);
        configuration.addAnnotatedClass(TheaterMovie.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static void AddToDB() {
        try {
            String actors = " Lewis Tan,Jessica McNamee, Josh Lawson";
            String str = "MMA fighter Cole Young seeks out Earth's greatest champions in order to stand against the enemies of Outworld in a high stakes battle for the universe.";
            byte[] pixelsArray1 = Files.readAllBytes(Paths.get("C:\\Users\\USER1\\eclipse-workspace\\Sw2021_project\\server\\src\\main\\java\\il\\cshaifasweng\\OCSFMediatorExample\\server\\MK.jpg"));
            TheaterMovie m = new TheaterMovie("Mortal Kombat", "מורטל קומבט", actors, "Action", str, "wb", pixelsArray1);
            Theater th = new Theater("Haifa");
            Hall hall = new Hall(40, th, 1);
            th.AddHalls(hall);
            Date d = new Date(10000000);
            MovieShow ms = new MovieShow(m, d, th, "15:00", "17:00", 40);
            session.save(th);
            session.save(hall);
            session.save(m);
            session.save(ms);
            session.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void test() {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            AddToDB();
            session.getTransaction().commit(); // Save everything.
        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null)
                session.close();
        }
    }

}
