

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * MainFrame
 * The Main frame for the application
 * @author Tristan Parker
 * @version 1.0
 */
public class MainFrame extends JFrame
{	
	// Optional, create a serial UID
	private static final long serialVersionUID = 4086502711485470761L;
	
	public static final File FILTER_FILE = new File(System.getProperty("user.dir"), "filters.txt");
	
	// The minimal and initial width of the window
	private static final int WIDTH = 800;
	// The minimal and initial height of the window
	private static final int HEIGHT = 600;
	
	// The words to filter
	private ArrayList<String> filters;
	
	// The content panel
	private JPanel panel;
	
	// The menu bar
	private JMenuBar menuBar;
	
	// The List display: filter words
	private JList<String> listFilter;
	
	// The status text: application
	private JLabel lblStatus;
	
	// The original text field
	private JEditorPane txtOriginal;

	// The filtered text field
	private JEditorPane txtFiltered;
	
	// To pick a text file 
	private JFileChooser fileChooser;
	
	// Bad word counter
	private JLabel lblBadWordCounter;
	
	// Counter for words using capital letters
	private JLabel lblLoudWordCounter;
	
	// Text type
	private JLabel lblTextType;
	
	// Live percentage for abusive, loud or acceptable language, or combination 
	private volatile TextStats textStats;

	/**
	 * The Constructor
	 * The first method called when making a new instance
	 * of this class
	 */
	public MainFrame()
	{
		// Initialize our array list
		filters = new ArrayList<>();
		
		textStats = new TextStats();
		
		// Create a new dimension for our minimum size
		Dimension size = new Dimension(WIDTH, HEIGHT);
		
		// Set our size and minimum size
		setSize(size);
		setMinimumSize(size);
		// Close our application when the window closes
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Show our window in the middle of the screen
		setLocationRelativeTo(null);
		
		// Create the Graphical User Interface
		createGUI();
		
		// Show our window
		setVisible(true);
		
		// Load the filter words
		loadWords();
	}
	
	/**
	 * Update the status text label
	 * @param status - The new status text
	 */
	private void setStatus(String status)
	{
		// check if the label is initialized
		if(lblStatus != null)
			lblStatus.setText(status);
	}
	
	/**
	 * Create the Graphic User Interface (GUI)
	 */
	private void createGUI()
	{
		// Create our menu
		createMenu();
		
		// Initialize the panel
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		setContentPane(panel);
		
		// Initialize our status label and add it to the north field
		lblStatus = new JLabel();
		panel.add(lblStatus, BorderLayout.NORTH);
		
		// Initialize our list filter and add it to the west field
		listFilter = new JList<String>();
		JScrollPane scrollLeft = new JScrollPane(listFilter);
		panel.add(scrollLeft, BorderLayout.WEST);
		
		// Initialize our file chooser for opening files
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setDialogTitle("Open File to analyse");
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		// Create a new panel for the center, we want to use a different layout
		// manager, and add it to the center field
		JPanel pnlCenter = new JPanel();
		pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
		
		JScrollPane scrollCenter = new JScrollPane(pnlCenter);
		
		panel.add(scrollCenter, BorderLayout.CENTER);
		
		// Add Label for the original text field with overriding
		pnlCenter.add(new JLabel("Original"));
		txtOriginal = new JEditorPane();
		txtOriginal.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				applyFilter();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				applyFilter();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{

			}
		});
		pnlCenter.add(txtOriginal);
		
		pnlCenter.add(new JLabel("Filtered"));
		txtFiltered = new JEditorPane();
		txtFiltered.setContentType("text/html");
		txtFiltered.setEditable(false);
		pnlCenter.add(txtFiltered);
		
		JPanel pnlRight = new JPanel();
		pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
		JScrollPane scrollRight = new JScrollPane(pnlRight);
		panel.add(scrollRight, BorderLayout.EAST);
		 
		pnlRight.add(new JLabel("Text information"));
		pnlRight.add(lblBadWordCounter = new JLabel("Bad Words %"));
		pnlRight.add(lblLoudWordCounter = new JLabel("Caps: %"));
		pnlRight.add(lblTextType = new JLabel(""));
		
		lblBadWordCounter.setForeground(Color.RED);
		lblLoudWordCounter.setForeground(Color.ORANGE);
	}
	
	/**
	 * Create the menu
	 */
	private void createMenu()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		
		JMenuItem menuItemOpen = new JMenuItem("Open");
		menu.add(menuItemOpen);
		menuItemOpen.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int returnValue = fileChooser.showOpenDialog(MainFrame.this);
				if(returnValue == JFileChooser.APPROVE_OPTION)
				{
					File f = fileChooser.getSelectedFile();
					
					setStatus("Reading file");
					String text = Util.readFile(f, false);
					txtOriginal.setText(text);
					setStatus("Done");
				}
			}
		});
		
		JMenu menuFilters = new JMenu("Filter(s)");
		menuBar.add(menuFilters);
		
		JMenuItem menuItemFilterAdd = new JMenuItem("Add");
		menuFilters.add(menuItemFilterAdd);
		menuItemFilterAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String newFilter = JOptionPane.showInputDialog(null, "New Filter", "Add Filter", JOptionPane.PLAIN_MESSAGE);
				if(newFilter != null)
				{
					newFilter = newFilter.toLowerCase();
					setStatus("Adding new filter '" + newFilter + "'");
					if(!filters.contains(newFilter))
						filters.add(newFilter);
					setStatus("Writing filters to file");
					Util.saveFile(FILTER_FILE, String.join("\n", filters));
					loadWords();
					setStatus("Done.");
				}
			}
		});
		
		JMenuItem menuItemFilterRemove = new JMenuItem("Remove");
		menuFilters.add(menuItemFilterRemove);
		menuItemFilterRemove.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String selectedWord = listFilter.getSelectedValue();
				if(selectedWord == null)
					return;
				
				if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete '" + selectedWord + "' from the filters?", "Remove Filter", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					setStatus("Removing filter '" + selectedWord + "'");
					filters.remove(selectedWord);
					setStatus("Writing filters to file");
					Util.saveFile(FILTER_FILE,String.join("\n", filters));
					loadWords();
					setStatus("Done");
				}
			}
		});
		
		JMenuItem menuItemFilterRefresh = new JMenuItem("Refresh");
		menuFilters.add(menuItemFilterRefresh);
		menuItemFilterRefresh.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Load filter words
				loadWords();
			}
		});
	}
	
	private void loadWords()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				setStatus("Loading filter words...");
				
				// Clear previous filters
				filters.clear();
				listFilter.removeAll();
				listFilter.setEnabled(false);
				
				txtOriginal.setEnabled(false);
				
				String filterWordsString = Util.readFile(FILTER_FILE, true);
				String[] filterWords = filterWordsString.split("(\\r\\n|\\r|\\n)");
				for(String filter : filterWords)
				{
					filter = filter.trim().toLowerCase().replace(" ", "");
					if(!filters.contains(filter))
					{
						setStatus("Adding filter: '" + filter + "'");
						filters.add(filter);
					}
				}
			
				String[] rawFilters = new String[filters.size()];
				filters.toArray(rawFilters);
				
				listFilter.setListData(rawFilters);
				listFilter.setEnabled(true);
				txtOriginal.setEnabled(true);
				setStatus("Done.");
				
				applyFilter();
			}
		}).start();
		
	}
	
	private synchronized void applyFilter()
	{
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
			
				// Thread safety
				synchronized (MainFrame.this)
				{
					setStatus("Applying filter");
					
					try
					{
						String original = txtOriginal.getDocument().getText(0,  txtOriginal.getDocument().getLength());
						// Use regular expression to split the text into sentences.
						// split on a full stop followed by a space or
						// split by a new line character.
						String[] sentences = original.split("(\\.\\s)|(\\r\\n|\\r|\\n)");
						
						StringBuilder builder = new StringBuilder();
						textStats.reset();
						
						for(String sentence : sentences)
						{
							StringBuilder sentenceBuilder = new StringBuilder();
							
							SentenceStats stats = new SentenceStats();
							
							System.out.println(sentence);
							
							String[] words = sentence.split(" ");
							for(String word : words)
							{
								stats.wordCount++;
								
								// \\W = special characters, 0 or 1
								// [A-Z]+ is capital alphanumeric
								boolean isUpperCase = word.matches("(\\W{0,1})([A-Z]+)(\\W{0,1})");
								boolean isBadword = false;
								for(String filter : filters)
								{
									if(word.trim().toLowerCase().matches("\\b" + filter + "+\\W{0,1}")) 
									{
										isBadword = true;
										break;
									}
								}
								
								if(isUpperCase)
								{
									if(word.length() > 1)
										stats.allCapsWordCount++;
									sentenceBuilder.append("<u style=\"color: orange;\">");
								}
								
								if(isBadword)
								{
									stats.badWordCount++;

									int wordLen = word.length() - 2;
									
									sentenceBuilder.append("<b style=\"color: red;\">");
									sentenceBuilder.append(word.substring(0, 1));
									for(int i = 0; i < wordLen; i++)
										sentenceBuilder.append("*");
									sentenceBuilder.append(word.substring(word.length() - 1, word.length()));
									sentenceBuilder.append("</b>");
								}
								else
									sentenceBuilder.append(word);
								
								if(isUpperCase)
									sentenceBuilder.append("</u>");
								
								sentenceBuilder.append(" ");
							}
							
							builder.append(sentenceBuilder.toString())
							.append("<br>");
							
							textStats.addSentenceStats(stats);
						}
						
						SwingUtilities.invokeLater(new Runnable()
						{	
							@Override
							public void run()
							{
								txtFiltered.setText(builder.toString());
							}
						});
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					
					setStatus("Done");
					updateBadWordCount();
					updateCapsWordCount();
					updateTextType();
				}
			
				
				
			}
	
		}).start();
		
	}
	
	private void updateBadWordCount()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				DecimalFormat df = new DecimalFormat("#.##");
				lblBadWordCounter.setText("Bad words: " + df.format(textStats.getBadPercentage()) + "%");
			}
		});
	}
	
	private void updateCapsWordCount()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				DecimalFormat df = new DecimalFormat("#.##");
				lblLoudWordCounter.setText("Loud words: " + df.format(textStats.getLoudPercentage()) + "%");
			}
		});
	}
	
	private void updateTextType()
	{
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				lblTextType.setText(textStats.getTextInformation());
			}
		});
	}
}