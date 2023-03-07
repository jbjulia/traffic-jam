/**
 * File Name: Main.java
 * Date: 05 MAR 2023
 * Author: Joseph Julian
 * Purpose: Main class extends JFrame and implements Runnable and ChangeListener interfaces. On startup, this program
 * simulates traffic data for four (4) vehicles as it pertains to three (3) distinct intersections. Clicking 'Start'
 * will begin the simulation, creating and synchronizing numerous threads that depict vehicles traveling at different
 * speeds, starting from different distances, entering the aforementioned intersections. This program provides
 * functionality to start, pause, resume, and stop all threads, all while updating a table with relevant traffic data.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JFrame implements Runnable, ChangeListener {
    private static final AtomicBoolean isRunning = new AtomicBoolean(false);

    static JLabel lblCurrentTime = new JLabel();
    static JLabel lblIntersectionA = new JLabel();
    static JLabel lblIntersectionB = new JLabel();
    static JLabel lblIntersectionC = new JLabel();
    static JSlider fordMustangSlider = new JSlider(0, 3000);
    static JSlider chevyCamaroSlider = new JSlider(0, 3000);
    static JSlider dodgeChargerSlider = new JSlider(0, 3000);
    static JSlider teslaModelSSlider = new JSlider(0, 3000);
    static Thread gui;

    private static boolean isAlive;

    private final JButton btnStart = new JButton("Start");
    private final JButton btnPause = new JButton("Pause");
    private final JButton btnStop = new JButton("Stop");

    Intersection A = new Intersection("intersectionA", lblIntersectionA);
    Intersection B = new Intersection("intersectionB", lblIntersectionB);
    Intersection C = new Intersection("intersectionC", lblIntersectionC);

    Car fordMustang = new Car("fordMustangThread", 500, 0);
    Car chevyCamaro = new Car("chevyCamaroThread", 1000, 0);
    Car dodgeCharger = new Car("dodgeChargerThread", 1500, 1000);
    Car teslaModelS = new Car("teslaModelSThread", 2000, 1000);

    Car[] carArray = {
            fordMustang,
            chevyCamaro,
            dodgeCharger,
            teslaModelS
    };

    Intersection[] intersectionArray = {
            A,
            B,
            C
    };

    Object[][] trafficArray = {
            {
                    "Ford Mustang",
                    fordMustang.getPosition(),
                    0,
                    0
            },
            {
                    "Chevy Camaro",
                    chevyCamaro.getPosition(),
                    0,
                    0
            },
            {
                    "Dodge Charger",
                    dodgeCharger.getPosition(),
                    0,
                    0
            },
            {
                    "Tesla Model S",
                    teslaModelS.getPosition(),
                    0,
                    0
            }
    };

    String[] columnNames = {
            "Car",
            "X Position",
            "Y Position",
            "Speed (km/h)"
    };

    private final JTable tblTraffic = new JTable(trafficArray, columnNames);

    public Main() {
        super("CMSC 335 Project 3 - TrafficJAM - by Joseph Julian");
        isAlive = Thread.currentThread().isAlive();
        constructGUI();
        addActionListeners();
    }

    public static void main(String[] args) {
        Main GUI = new Main();
        GUI.display();
        gui = new Thread(GUI);
        Thread time = new Thread(new Time());
        time.start();
    }

    private void display() {
        setSize(600, 400);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void constructGUI() {
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font subtitleFont = new Font("Arial", Font.PLAIN, 16);
        Font labelFont = new Font("Arial", Font.PLAIN, 14);

        JLabel lblTitle = new JLabel("TrafficJAM (Java-Assisted Management)");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(Color.BLUE);
        JLabel lblDirections = new JLabel("Click 'Start' to begin!");
        lblDirections.setFont(subtitleFont);
        JLabel lblTime = new JLabel("Current Time: ");
        lblTime.setFont(labelFont);
        JLabel lblTrafficLightA = new JLabel("Intersection A: ");
        lblTrafficLightA.setFont(labelFont);
        JLabel lblTrafficLightB = new JLabel("Intersection B: ");
        lblTrafficLightB.setFont(labelFont);
        JLabel lblTrafficLightC = new JLabel("Intersection C: ");
        lblTrafficLightC.setFont(labelFont);

        fordMustangSlider.addChangeListener(this);
        fordMustangSlider.setValue(fordMustang.getPosition());
        fordMustangSlider.setMajorTickSpacing(1000);
        fordMustangSlider.setPaintTicks(true);
        chevyCamaroSlider.addChangeListener(this);
        chevyCamaroSlider.setValue(chevyCamaro.getPosition());
        chevyCamaroSlider.setMajorTickSpacing(1000);
        chevyCamaroSlider.setPaintTicks(true);
        dodgeChargerSlider.addChangeListener(this);
        dodgeChargerSlider.setValue(dodgeCharger.getPosition());
        dodgeChargerSlider.setMajorTickSpacing(1000);
        dodgeChargerSlider.setPaintTicks(true);
        teslaModelSSlider.addChangeListener(this);
        teslaModelSSlider.setValue(teslaModelS.getPosition());
        teslaModelSSlider.setMajorTickSpacing(1000);
        teslaModelSSlider.setPaintTicks(true);

        tblTraffic.setPreferredScrollableViewportSize(new Dimension(400, 64));
        tblTraffic.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tblTraffic);

        JPanel sliderPanel = new JPanel(new GridLayout(4, 1));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Car Position"));
        sliderPanel.add(fordMustangSlider);
        sliderPanel.add(chevyCamaroSlider);
        sliderPanel.add(dodgeChargerSlider);
        sliderPanel.add(teslaModelSSlider);

        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Traffic Data"));
        dataPanel.add(scrollPane);

        btnStart.setToolTipText("Start simulation");
        btnPause.setToolTipText("Pause simulation");
        btnStop.setToolTipText("Stop simulation");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap(30, 30)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lblTitle)
                        .addComponent(lblDirections)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblTime)
                                        .addComponent(lblCurrentTime)))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnStart)
                                        .addComponent(btnPause)
                                        .addComponent(btnStop)))
                        .addComponent(fordMustangSlider)
                        .addComponent(chevyCamaroSlider)
                        .addComponent(dodgeChargerSlider)
                        .addComponent(teslaModelSSlider)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblTrafficLightA)
                                        .addComponent(lblIntersectionA)
                                        .addContainerGap(20, 20)
                                        .addComponent(lblTrafficLightB)
                                        .addComponent(lblIntersectionB)
                                        .addContainerGap(20, 20)
                                        .addComponent(lblTrafficLightC)
                                        .addComponent(lblIntersectionC))
                                .addComponent(dataPanel)))
                .addContainerGap(30, 30)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addComponent(lblDirections))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTime)
                        .addComponent(lblCurrentTime))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnStart)
                        .addComponent(btnPause)
                        .addComponent(btnStop))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(fordMustangSlider)
                        .addComponent(chevyCamaroSlider)
                        .addComponent(dodgeChargerSlider)
                        .addComponent(teslaModelSSlider))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTrafficLightA)
                        .addComponent(lblIntersectionA)
                        .addComponent(lblTrafficLightB)
                        .addComponent(lblIntersectionB)
                        .addComponent(lblTrafficLightC)
                        .addComponent(lblIntersectionC))
                .addComponent(dataPanel)

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addGap(20, 20, 20))
                .addGap(20, 20, 20)
        );
        pack();
        btnStop.setEnabled(false);
        btnPause.setEnabled(false);
    }

    private void addActionListeners() {
        btnStart.addActionListener((ActionEvent e) -> {
            if (!isRunning.get()) {
                System.out.println("START:\t\t" + Thread.currentThread().getName());
                A.startThread();
                B.startThread();
                C.startThread();
                fordMustang.startThread();
                chevyCamaro.startThread();
                dodgeCharger.startThread();
                teslaModelS.startThread();
                gui.start();
            }
            btnStart.setEnabled(false);
            btnPause.setEnabled(true);
            btnStop.setEnabled(true);
            isRunning.set(true);
        });

        btnPause.addActionListener((ActionEvent e) -> {
            if (isRunning.get()) {
                for (Car i : carArray) {
                    i.suspendThread();
                    System.out.println("SUSPEND:\t" + Thread.currentThread().getName());
                }
                for (Intersection i : intersectionArray) {
                    i.interruptThread();
                    i.suspendThread();
                }
                btnPause.setText("Continue");
                btnStart.setEnabled(false);
                btnPause.setEnabled(true);
                btnStop.setEnabled(false);
                isRunning.set(false);
            } else {
                for (Car i : carArray) {
                    if (i.isSuspended.get()) {
                        i.resumeThread();
                        System.out.println("RESUME:\t\t" + Thread.currentThread().getName());
                    }
                }
                for (Intersection i : intersectionArray) {
                    i.resumeThread();
                }
                btnPause.setText("Pause");
                btnStart.setEnabled(false);
                btnPause.setEnabled(true);
                btnStop.setEnabled(true);
                isRunning.set(true);
            }
        });

        btnStop.addActionListener((ActionEvent e) -> {
            if (isRunning.get()) {
                System.out.println("STOP:\t\t" + Thread.currentThread().getName());
                for (Car i : carArray) {
                    i.stopThread();
                }
                for (Intersection i : intersectionArray) {
                    i.stopThread();
                }
                btnStart.setEnabled(false);
                btnPause.setEnabled(false);
                btnStop.setEnabled(false);
                isRunning.set(false);
            }
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        trafficArray[0][1] = fordMustangSlider.getValue();
        trafficArray[1][1] = chevyCamaroSlider.getValue();
        trafficArray[2][1] = dodgeChargerSlider.getValue();
        trafficArray[3][1] = teslaModelSSlider.getValue();
        trafficArray[0][3] = fordMustang.getSpeed();
        trafficArray[1][3] = chevyCamaro.getSpeed();
        trafficArray[2][3] = dodgeCharger.getSpeed();
        trafficArray[3][3] = teslaModelS.getSpeed();
        tblTraffic.repaint();
    }

    private void getPosition() {
        if (isRunning.get()) {
            switch (A.getColor()) {
                case "Red" -> {
                    for (Car i : carArray) {
                        if (i.getPosition() > 500 && i.getPosition() < 1000) {
                            i.isAtLight.set(true);
                        }
                    }
                }
                case "Green" -> {
                    for (Car i : carArray) {
                        if (i.isAtLight.get()) {
                            i.resumeThread();
                        }
                    }
                }
            }
            switch (B.getColor()) {
                case "Red" -> {
                    for (Car i : carArray) {
                        if (i.getPosition() > 1500 && i.getPosition() < 2000) {
                            i.isAtLight.set(true);
                        }
                    }
                }
                case "Green" -> {
                    for (Car i : carArray) {
                        if (i.isAtLight.get()) {
                            i.resumeThread();
                        }
                    }
                }
            }
            switch (C.getColor()) {
                case "Red" -> {
                    for (Car i : carArray) {
                        if (i.getPosition() > 2500 && i.getPosition() < 3000) {
                            i.isAtLight.set(true);
                        }
                    }
                }
                case "Green" -> {
                    for (Car i : carArray) {
                        if (i.isAtLight.get()) {
                            i.resumeThread();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (isAlive) {
            if (isRunning.get()) {
                fordMustangSlider.setValue(fordMustang.getPosition());
                chevyCamaroSlider.setValue(chevyCamaro.getPosition());
                dodgeChargerSlider.setValue(dodgeCharger.getPosition());
                teslaModelSSlider.setValue(teslaModelS.getPosition());
                getPosition();
            }
        }
    }
}