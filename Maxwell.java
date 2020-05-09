import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Maxwell implements ActionListener {
    
    JFrame window = new JFrame("Maxwell's Demon");
    JPanel gamePanel;
    JPanel buttonPanel = new JPanel();
    JPanel temperaturePanel = new JPanel();
    JButton addButton = new JButton("ADD");
    JButton resetButton = new JButton("RESET");
    Temperature leftTemp = new Temperature();
    Temperature rightTemp = new Temperature();   
    
    public Maxwell() {
    	
    	gamePanel = new Game();
    	
        gamePanel.setBackground(Color.WHITE);
        buttonPanel.setBackground(Color.GRAY);
        temperaturePanel.setBackground(Color.BLACK);
        
        window.add(gamePanel,BorderLayout.CENTER);
        window.add(buttonPanel,BorderLayout.PAGE_END);
        window.add(temperaturePanel,BorderLayout.PAGE_START);
        
        temperaturePanel.setLayout( new GridLayout(1,2) );
        temperaturePanel.add(leftTemp);
        temperaturePanel.add(rightTemp);
        
        buttonPanel.setLayout( new GridLayout(1,2) );
        buttonPanel.add(resetButton);
        buttonPanel.add(addButton);
        
        resetButton.addActionListener(this);
        addButton.addActionListener(this);
        
        resetButton.setActionCommand("reset");
        addButton.setActionCommand("add");
                
        window.setSize(825, 500);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    } //Maxwell Constructor
    
    public void actionPerformed(ActionEvent e) {
        if ( e.getActionCommand().equals("add") ) {
    		((Game) gamePanel).addBalls();
        }
        
        if ( e.getActionCommand().equals("reset") ){
        	((Game) gamePanel).resetBalls();
        }
    } //actionPerformed

    public static void main(String[] args) {

    	Maxwell run = new Maxwell();

    }

    public class Game extends JPanel implements ActionListener {
		
		Timer clicky;
    	double deltat = 0.1; //  in seconds

    	FastBall[] fastBalls;
    	SlowBall[] slowBalls;		
    	int ballCount;
    	
		public int rightFast = 0, leftFast = 0;
		public int rightSlow = 0, leftSlow = 0;
		public int leftTempNum = 0;
		public int rightTempNum = 0;	
    	
    	Border border = new Border();
    	
    	Door door = new Door();
    	boolean doorOpen = false;
    	
    	public Game() {
    		System.out.println("Maxwell's Demon: starting ... ");
    		setBackground( Color.WHITE );
    		
    		ballCount = 0;
    		fastBalls = new FastBall[500];
    		slowBalls = new SlowBall[500];
    		
    		clicky = new Timer((int)(1000 * deltat), this);
    		clicky.start();
    		
    		addMouseListener(
    				new MouseAdapter() {	    
    					public void mousePressed( MouseEvent m ) {
    						door.openDoor();
    					}
    					public void mouseReleased( MouseEvent m ) {
    						door.closeDoor();
    					}
    				}
    		); //addMouseListener
    		
    		setSize( 800, 500 );
    		setLayout(null);
    		setVisible(true);
    	} //Game Constructor
    	
    	public void actionPerformed( ActionEvent e ) {
    		if ( e.getSource()==clicky ) { 
    			moveAll();
    		}

    		repaint();
    	} //actionPerformed
    	
    	@Override
    	public void paint( Graphics g ) {
    	   
    		g.setColor( Color.WHITE ); // just white-out the window
    		int w = getWidth();  
    		int h = getHeight();
    		g.fillRect( 0, 0, w, h );  // with a big rectangle
    		
    		border.drawMe(g);
    		door.drawMe(g);
    		
    		for ( int i=0; i<ballCount; i++ ) { 
    			fastBalls[i].drawMe(g);
    			slowBalls[i].drawMe(g);
    		}

    	} //paint
    	
    	public void addBalls() {
    		
			fastBalls[ballCount] = new FastBall( (int)(Math.random()*300 + 50), (int)(Math.random()*300 + 50) );
			fastBalls[ballCount+1] = new FastBall( (int)(Math.random()*300 + 450), (int)(Math.random()*300 + 50) );
			slowBalls[ballCount] = new SlowBall( (int)(Math.random()*300 + 50), (int)(Math.random()*300 + 50));
			slowBalls[ballCount+1] = new SlowBall( (int)(Math.random()*300 + 450), (int)(Math.random()*300 + 50) );
			
			ballHandler( fastBalls[ballCount] );
			ballHandler( fastBalls[ballCount+1] );
			ballHandler( slowBalls[ballCount] );
			ballHandler( slowBalls[ballCount+1] );
			
			ballCount+=2;
			
			updateTemp();
    	} //addBalls
    	
    	public void resetBalls() {
    		for (int i = 0; i < ballCount; i++) {
    			fastBalls[i] = null;
    			slowBalls[i] = null;
    		}
    		
    		ballCount = 0;
    		
    		updateTemp();
    	} //resetBalls
    	
    	public void ballHandler(Ball ball) {
    		if ( (ball.x) >389 && (ball.x)<411 ) {
    			if ( doorOpen = true ) {
    				ballCross(ball);
    			}
    		}
    		else if ( ball.x > 349 ) {
    			rightAdder(ball);
    		}
    		else {
    			leftAdder(ball);
    		}
    	} //ballHandler
    	
    	public void ballCross(Ball ball) {

    		if ( ball.vx > 0 ) {
    			ball.leftSide = false;
    		}
    		else {
    			ball.leftSide = true;
    		}
    		
    		updateTemp();
    	}
    	
    	public void rightAdder(Ball ball) {
    		ball.leftSide = false;
    	} //rightAdder
    	
    	public void leftAdder(Ball ball) {
    		ball.leftSide = true;
    	} //leftAdder
    	
    	public void updateTemp() {
			int leftTemperature = findLeftTemp();
			leftTemp.setTemp(Integer.toString( leftTemperature ) );
			
			int rightTemperature = findRightTemp();
			rightTemp.setTemp(Integer.toString( rightTemperature ) );

		} //change temp
    	
    	public int findLeftTemp() {
    		int temp = 0;
    		int numerator = 0;
    		int leftBallCount = 0;
    		
    		for ( int i = 0; i < ballCount; i++) {
    			if ( fastBalls[i].leftSide ) {
    				numerator += fastBalls[i].vx * fastBalls[i].vx;
    				leftBallCount++;
    			}
    			if ( slowBalls[i].leftSide ) {
    				numerator += slowBalls[i].vx * slowBalls[i].vx;
    				leftBallCount++;
    			}
    		}
    		
    		if (leftBallCount != 0) {
    			temp = numerator / leftBallCount;
    		}
    		else 
    			return 0;
    		
    		return temp;
    	} //findLeftTemp
    	
    	public int findRightTemp() {
    		int temp = 0;
    		int numerator = 0;
    		int rightBallCount = 0;
    		
    		for ( int i = 0; i < ballCount; i++) {
    			if ( !fastBalls[i].leftSide ) {
    				numerator += fastBalls[i].vx * fastBalls[i].vx;
    				rightBallCount++;
    			}
    			if ( !slowBalls[i].leftSide ) {
    				numerator += slowBalls[i].vx * slowBalls[i].vx;
    				rightBallCount++;
    			}
    		}
    		
    		if (rightBallCount != 0) {
    			temp = numerator / rightBallCount;
    		}
    		
    		return temp;
    	} //findRightTemp
    	
    	public void moveAll() {
    		for ( int i=0; i<ballCount; i++ ) { 
    			fastBalls[i].move(deltat); 
    			slowBalls[i].move(deltat);
    		}
    	} //moveAll

    	public class Border {
    		
    		public void drawMe( Graphics g ) {	
    			g.setColor( Color.BLACK );
    			g.drawRect( (int)(10), (int)(31), 780, 370 );
    		} //drawMe
    		
    	} //Border
    	
    	public class Ball {
    		
    		double x, y;
    		double vx, vy;
    		double oldx, oldy;
    		boolean leftSide = false;

    		
    		public void move( double deltat ) {
    			oldx = x; oldy = y;
    			x += vx;
    			y += vy;
    			stayOnScreen();
    		} //move
    		
    		public void stayOnScreen() {
    			if ( x<15 ) changeXDir();
    			if ( y<35 ) changeYDir();
    			if ( x>780 ) changeXDir();
    			if ( y>390 ) changeYDir();
    			
    			if (doorOpen == false) {
    				if ( (x+6)>395 && (x-6)<405 ) {
    					changeXDir();
    				}
    				if ( x > 395 && x < 405 ) {
    					if (vx > 0 ) {
    						x += 17;
    					}
    					else {
    						x -= 17;
    					}
    				}
    			}
    			else {
    				if ( (x+6)>395 && (x-6)<405 ) {
    					ballHandler(this);
    					updateTemp();
    				}
    			}
    			
    		} //stayOnScreen
    		
    		public void changeXDir() {
    			vx *= -1;
    		} //changeXDir
    		
    		public void changeYDir() {
    			vy *= -1;
    		} //changeYDir
    		
    	} //Ball super
    	
    	public class FastBall extends Ball {
    		
    		public FastBall( int x1, int y1 ) {
    			x = x1; 
    			y = y1; 
    			vx = Math.random()*2 + 4;
    			vy = Math.random()*2 + 4;
    		} //fastBall
    		
    		public void drawMe( Graphics g ) {
    			g.setColor( Color.RED );
    			g.fillOval( (int)(x-2), (int)(y-2), 10, 10 );
    		} //drawMe
    		
    	} //FastBall
    	
    	public class SlowBall extends Ball {
    		
    		public SlowBall( int x1, int y1 ) {
    			x = x1; 
    			y = y1; 
    			vx = Math.random()*2 + 2;
    			vy = Math.random()*2 + 2;
    		} //slowBall
    		
    		public void drawMe( Graphics g ) {	
    			g.setColor( Color.BLUE );
    			g.fillOval( (int)(x-2), (int)(y-2), 10, 10 );
    		} //drawMe
    		
    	} //SlowBall
    	
    	public class Door {
    		
    		Color doorColor = Color.BLACK;
    		
    		public void drawMe( Graphics g ) {	
    			g.setColor( doorColor );
    			g.fillRect( (int)(402), (int)(31), 5, 370 );
    		} //drawMe
    		
    		public void openDoor() {
    			doorOpen = true;
    			doorColor = Color.GRAY;
    		} //openDoor
    		
    		public void closeDoor() {
    			doorOpen = false;
    			doorColor = Color.BLACK;
    		} //closeDoor
    		
    	} //Door
    	
    } //Game
    
	public class Temperature extends JComponent {
		String value;
		
		Temperature() {
			setPreferredSize( new Dimension(0, 25));
			value = "0";
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.setColor( Color.CYAN );
			
			g.setFont( new Font( "TimesRoman", Font.PLAIN, 18 ) );
			g.drawString("Temperature: " + value,  50,  20);
		} //paintComponent
		
		public void setTemp(String newValue) {
			value = newValue;
			repaint();
		}
		
	} //Temperature
	
} //Maxwell


