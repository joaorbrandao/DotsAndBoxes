/*
Joao Brandao
2015/2016


----- MAIN/APPLET -----

This class allows the code to be ran as standalone
or applet.

*/

import javax.swing.JApplet;

public class MainApplet extends JApplet {
	private static final long serialVersionUID = 1L;

	FirstPage firstPage = null;

    public static void main(String args[]) {
        MainApplet app = new MainApplet();
        app.init();
    }

    public void init() {
    	firstPage = new FirstPage();
    }
}
