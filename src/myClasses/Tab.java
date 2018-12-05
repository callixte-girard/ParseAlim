package myClasses;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class Tab extends JPanel 
{
    static Object[] columnNames ;
	static Object[][] data ;
    
	
	public static void setDataFromObjectList(ArrayList<?> al)
	{
		// columnNames
		Object first = al.get(0);
		ArrayList<Field> fields = ClassReader.returnFields(first, false);
		ArrayList<String> headers = new ArrayList<String>();
		
		for (Field f : fields)
		{
			headers.add(f.getName());
		}
		
		columnNames = headers.toArray();
		
		// data
		ArrayList<Object[]> al_tmp = new ArrayList<Object[]>();
		
		for (Object o : al)
		{
			ArrayList<String> cells = new ArrayList<String>();
			
			for (Object name : columnNames)
			{
				System.out.println(name.toString());
				
				Object val = ClassReader.returnFieldValFromName(o, name.toString());
				String content = val.toString();
				
				cells.add(content);
			}
			
			Object[] sub_tab = cells.toArray();
			al_tmp.add(sub_tab);
		}
		
		Object[][] tmp = new Object[al_tmp.size()][columnNames.length] ;
		
		int i=0 ;
		for (Object[] o : al_tmp)
		{
			int j=0 ;
			for (Object cell : o)
			{
				tmp[i][j] = cell.toString() ;
				j++ ;
			}
			i++ ;
		}
		
		data = tmp ;

	}
	
	
    public Tab()
    {
        super(new GridLayout(1,0));

        final JTable table = new JTable(data, columnNames)
        {
        	// pour qu'elle se resize automatiquement
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) 
            {
                   Component component = super.prepareRenderer(renderer, row, column);
                   int rendererWidth = component.getPreferredSize().width;
                   TableColumn tableColumn = getColumnModel().getColumn(column);
                   tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                   return component;
            }
        } ;
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
    	table.setPreferredScrollableViewportSize(new Dimension(
    		//	java.awt.Toolkit.getDefaultToolkit().getScreenSize().width
    			1350
    			, 500
    		));
    	
        table.setFillsViewportHeight(true);
        
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }
    
    public static void createGUI()
    {
    	//Create and set up the window.
        JFrame frame = new JFrame("tableau java youpidou");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        Tab newContentPane = new Tab();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void showGUI()
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater
        (new Runnable() 
	        {
	            public void run()
	            {
	                createGUI();
	            }
	        }
        );
    }
}
