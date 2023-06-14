//*******************************************************************************************************************************************
// KMeans.java         Author:Ali Berk Karaarslan     Date:18.05.2023
//
// KMeans Clustering Simulation. Clusters "Data" to corresponding "Center"
//
//  How to use it ?
// -Press "AddData" button and then click to desired postition. It will add data to screen (Could be added as desired).
// -Press "AddCenter" button and then click to desired postition. It will add colorful centers to screen (Could be added as desired).
// -After the adding part, press "NextStep" button. It will make the next process of the clustering.
// -After few presses, It will cluster. Press "Reset" to start over.
//*******************************************************************************************************************************************

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class KMeans {

    //Initializing main components
    MainFrame frame;
    MainPanel panel;
    ButtonsPanel buttonsPanel;

    //Creating the main frame
    public KMeans(){frame =new MainFrame();}

    class MainFrame extends JFrame{

        public MainFrame(){

            //Setting the default options
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            setSize(700,700);
            setLocationRelativeTo(null);
            setTitle("KMeans Clustering By Ali Berk Karaarslan 2023");

            //Adding MainPanel and ButtonPanel
            panel = new MainPanel();
            add(panel,BorderLayout.CENTER);

            buttonsPanel = new ButtonsPanel();
            add(buttonsPanel,BorderLayout.SOUTH);

            setVisible(true);
        }
    }

    class MainPanel extends JPanel implements MouseInputListener{

        boolean addingData = true;
        boolean addingCenter = false;
        ArrayList<Center> allCenters = new ArrayList<>();
        ArrayList<Data> allData = new ArrayList<>();
        Random generator;

        public MainPanel(){
            //This random generator is for randomly picking colors of centers
            generator = new Random();
            addMouseListener(this);
            addMouseMotionListener(this);
            setVisible(true);
        }

        public void paint(Graphics g) {
            super.paint(g);
            drawData(g);
            drawCenters(g);
        }

        //Draws all the centers
        public void drawCenters(Graphics g){
            for(Center currCenter : allCenters){
                g.setColor(currCenter.color);
                g.fillOval(currCenter.xPosition-10, currCenter.yPosition-10, 20,20);
            }
        }

        //Draws all the data
        public void drawData(Graphics g){
            for(Data currData : allData){
                g.setColor(currData.color);
                g.fillOval(currData.xPosition-5, currData.yPosition-5, 10,10);
            }
        }

        //Calculates the centroids of the cluster's data. And moves the centers to those points.
        public void reLocate(){

            //If there exists center and data
            if(!allCenters.isEmpty() && !allData.isEmpty()) {

                //Moves across centers. Checks all clusters
                for (Center currCenter : allCenters) {

                    //Data count of  corresponded center's cluster
                    int dataCount = 0;
                    //Summation of all x and y positions of data in the same cluster
                    int totalX = 0;
                    int totalY = 0;

                    for (Data currData : allData) {
                        //Checks the data in the corresponded center's cluster
                        if (currData.clusterID == currCenter.clusterID) {
                            totalX += currData.xPosition;
                            totalY += currData.yPosition;
                            dataCount++;
                        }
                    }

                    //If there exist data in the cluster then moves the center to new centroid point
                    if (dataCount != 0) {
                        currCenter.xPosition = totalX / dataCount;
                        currCenter.yPosition = totalY / dataCount;
                    }
                }

                calculateDistances();
                repaint();
            }
        }

        //Calls findNearestCenter() of all data
        public void calculateDistances(){
            for(Data currData : allData){
                currData.findNearestCenter();
                repaint();
            }
        }

        @Override
        //If user presses to mouse button
        public void mousePressed(MouseEvent e) {
            //Creating data points
            if(addingData){
                Data newData = new Data(e.getX(),e.getY());
                allData.add(newData);
            }
            //Creating center points
            if(addingCenter){
                Center newCenter = new Center(e.getX(),e.getY());
                newCenter.color = new Color(generator.nextInt(255),generator.nextInt(255),generator.nextInt(255));
                newCenter.clusterID = allCenters.size();
                allCenters.add(newCenter);
            }
            repaint();
        }
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseDragged(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}
    }

    //Contains all the buttons of the program
    class ButtonsPanel extends JPanel{

        public ButtonsPanel(){

            setLayout(new FlowLayout());

            //Switches to addButton operation
            JButton button1 = new JButton("AddData");
            button1.addActionListener((ActionEvent e)-> {panel.addingData = true;
                                                            panel.addingCenter = false;});

            //Switches to addCenter operation
            JButton button2 = new JButton("AddCenter");
            button2.addActionListener((ActionEvent e)-> {panel.addingData = false;
                                                            panel.addingCenter = true;});
            //Performs the next step
            JButton button3 = new JButton("NextStep");
            button3.addActionListener((ActionEvent e)-> {
                panel.reLocate();
            });

            //Resets the program
            JButton button4 = new JButton("Reset");
            button4.addActionListener((ActionEvent e)-> {
                panel.allCenters.clear();
                panel.allData.clear();
                panel.repaint();
            });

            add(button1);
            add(button2);
            add(button3);
            add(button4);
            setVisible(true);
        }
    }

    //Class of Center Object
    class Center{

        int clusterID;
        int xPosition;
        int yPosition;
        Color color = Color.DARK_GRAY;

        public Center(int x,int y){
            xPosition = x;
            yPosition = y;
        }
    }

    //Class of Data Object
    class Data{

        int clusterID;
        int xPosition;
        int yPosition;
        Color color = Color.BLACK;
        double[] distanceToCenters;

        public Data(int x,int y){
            xPosition = x;
            yPosition = y;
        }

        //Calculates all the distances between centers. Then finds the nearest center and sets its clusterID and changes its color to nearestCenter's color.
        public void findNearestCenter(){

            distanceToCenters = new double[panel.allCenters.size()];

            //Calculating all the distances between centers
            for(int i=0;i<distanceToCenters.length;i++){
                Center currCenter = panel.allCenters.get(i);
                distanceToCenters[i] = Math.sqrt( (Math.pow(xPosition-currCenter.xPosition,2)+Math.pow(yPosition-currCenter.yPosition,2)) );
            }
            double minValue = Integer.MAX_VALUE;
            int minIndex =0;

            //Finding the smallest distance
            for(int i=0;i<distanceToCenters.length;i++){
                if(distanceToCenters[i]<=minValue){
                    minValue = distanceToCenters[i];
                    minIndex = i;
                }
            }
            //Setting the new variables
            clusterID = minIndex;
            color = panel.allCenters.get(clusterID).color;
        }
    }

    //Starts the program
    public static void main(String[] args) {
        new KMeans();
    }

}
