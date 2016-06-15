/*

2015/2016


----- MAIN/APPLET -----

ou Applet.
 
*/

import javax.swing.JApplet;

public class MainApplet extends JApplet {
	private static final long serialVersionUID = 1L;

	FirstPage firstPage = null;
	//Main newMain = null;

    public static void main(String args[]) {
        MainApplet app = new MainApplet();
        app.init();
    }

    public void init() {
    	//newMain = new Main();
    	firstPage = new FirstPage();
    }
}
