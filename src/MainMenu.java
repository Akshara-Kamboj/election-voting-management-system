import java.awt.*;
import java.awt.event.*;

/**
 * MainMenu.java
 * Election Voting Mechanism System - Main Menu screen (Java AWT only).
 *
 * Opens: AdminLogin, VoterRegistration, VoterLogin
 * Theme: navy-blue header, white card, light-grey background, green accents.
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class MainMenu extends Frame implements ActionListener {

    // ---------- Theme colours (reuse these in every screen) ----------
    private static final Color NAVY       = new Color(10, 36, 99);
    private static final Color GREEN      = new Color(40, 167, 69);
    private static final Color LIGHT_GREY = new Color(238, 240, 243);
    private static final Color WHITE      = Color.WHITE;
    private static final Color TEXT_GREY  = new Color(90, 90, 90);

    // ---------- Theme fonts ----------
    private static final Font TITLE_FONT    = new Font("SansSerif", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font TEXT_FONT     = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT   = new Font("SansSerif", Font.BOLD, 15);
    private static final Font SMALL_FONT    = new Font("SansSerif", Font.PLAIN, 11);

    // ---------- Buttons ----------
    private Button adminButton;
    private Button registerButton;
    private Button voterButton;
    private Button exitButton;

    /** Builds and shows the main menu window. */
    public MainMenu() {
        super("Election Voting System - Main Menu");

        setSize(520, 580);
        setResizable(false);
        setBackground(LIGHT_GREY);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Clicking the window's X button shows the same exit confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });

        // Centre the window on the screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2,
                    (screen.height - getHeight()) / 2);

        setVisible(true);
    }

    // =====================================================================
    // UI BUILDING METHODS
    // =====================================================================

    /** Navy header with the heading, subtitle and a thin green accent line. */
    private Panel createHeaderPanel() {
        // Anonymous Panel that overrides getInsets() to add inner padding
        Panel header = new Panel(new GridLayout(2, 1, 0, 6)) {
            @Override
            public Insets getInsets() {
                return new Insets(28, 10, 22, 10); // top, left, bottom, right
            }
        };
        header.setBackground(NAVY);

        Label title = new Label("Election Voting System", Label.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);

        // \u2022 is the bullet character; the escape avoids file-encoding problems
        Label subtitle = new Label("Secure \u2022 Simple \u2022 Transparent", Label.CENTER);
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(new Color(144, 238, 144)); // light green

        header.add(title);
        header.add(subtitle);

        // Thin green line below the header
        Panel accentLine = new Panel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 5);
            }
        };
        accentLine.setBackground(GREEN);

        // Wrap header + accent line so both sit in the NORTH region
        Panel wrapper = new Panel(new BorderLayout());
        wrapper.add(header, BorderLayout.CENTER);
        wrapper.add(accentLine, BorderLayout.SOUTH);
        return wrapper;
    }

    /** Light-grey area holding a white "card" with the instruction and buttons. */
    private Panel createCenterPanel() {
        // GridBagLayout with no constraints keeps the card centred
        Panel center = new Panel(new GridBagLayout());
        center.setBackground(LIGHT_GREY);

        // White card with inner padding
        Panel card = new Panel(new BorderLayout(0, 22)) {
            @Override
            public Insets getInsets() {
                return new Insets(30, 40, 35, 40);
            }
        };
        card.setBackground(WHITE);

        // Small instruction text
        Label instruction = new Label("Select an option to continue.", Label.CENTER);
        instruction.setFont(TEXT_FONT);
        instruction.setForeground(TEXT_GREY);
        card.add(instruction, BorderLayout.NORTH);

        // Four buttons stacked vertically
        Panel buttonPanel = new Panel(new GridLayout(4, 1, 0, 16));
        buttonPanel.setBackground(WHITE);

        adminButton    = createMenuButton("Admin Login", GREEN);
        registerButton = createMenuButton("Voter Registration", GREEN);
        voterButton    = createMenuButton("Voter Login", GREEN);
        exitButton     = createMenuButton("Exit", NAVY);

        buttonPanel.add(adminButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(voterButton);
        buttonPanel.add(exitButton);

        card.add(buttonPanel, BorderLayout.CENTER);
        center.add(card);
        return center;
    }

    /** Small grey footer text. */
    private Panel createFooterPanel() {
        Panel footer = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setBackground(LIGHT_GREY);

        Label note = new Label("College Project - Election Voting Mechanism System");
        note.setFont(SMALL_FONT);
        note.setForeground(TEXT_GREY);
        footer.add(note);
        return footer;
    }

    /** Creates one styled menu button and attaches the ActionListener. */
    private Button createMenuButton(String text, Color background) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(WHITE);
        button.setPreferredSize(new Dimension(280, 46)); // same size for all buttons
        button.addActionListener(this);
        return button;
    }

    // =====================================================================
    // BUTTON ACTIONS
    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == adminButton) {
            dispose();            // close this menu first
            new AdminLogin();     // then open the Admin Login screen
        } else if (source == registerButton) {
            dispose();
            new VoterRegistration();
        } else if (source == voterButton) {
            dispose();
            new VoterLogin();
        } else if (source == exitButton) {
            showExitConfirmation();
        }
    }

    /**
     * AWT has no ready-made confirmation box, so we build a small modal Dialog.
     * "Modal" means the main menu cannot be used until this dialog is closed.
     */
    private void showExitConfirmation() {
        final Dialog dialog = new Dialog(this, "Confirm Exit", true);
        dialog.setSize(380, 180);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());

        // Centre the dialog over the main menu
        dialog.setLocation(getX() + (getWidth() - 380) / 2,
                           getY() + (getHeight() - 180) / 2);

        Label message = new Label("Are you sure you want to exit?", Label.CENTER);
        message.setFont(new Font("SansSerif", Font.BOLD, 15));
        message.setForeground(NAVY);
        dialog.add(message, BorderLayout.CENTER);

        Button yesButton = new Button("Yes, Exit");
        yesButton.setFont(BUTTON_FONT);
        yesButton.setBackground(GREEN);
        yesButton.setForeground(WHITE);

        Button noButton = new Button("Cancel");
        noButton.setFont(BUTTON_FONT);
        noButton.setBackground(NAVY);
        noButton.setForeground(WHITE);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttons.setBackground(WHITE);
        buttons.add(yesButton);
        buttons.add(noButton);
        dialog.add(buttons, BorderLayout.SOUTH);

        // Yes -> close dialog, close menu, end the program
        yesButton.addActionListener(ev -> {
            dialog.dispose();
            dispose();
            System.exit(0);
        });

        // Cancel -> just close the dialog and stay on the menu
        noButton.addActionListener(ev -> dialog.dispose());

        // X button on the dialog behaves like Cancel
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true); // blocks here until the dialog is closed
    }

    // =====================================================================
    // PROGRAM ENTRY POINT
    // =====================================================================

    public static void main(String[] args) {
        new MainMenu();
    }
}

/*
 * ---------------------------------------------------------------
 * HOW TO COMPILE AND RUN (VS Code terminal, inside the src folder)
 * ---------------------------------------------------------------
 *   cd src
 *   javac *.java        <- compiles every screen together
 *   java MainMenu       <- starts the application
 *
 * NOTE: MainMenu refers to AdminLogin, VoterRegistration and VoterLogin,
 * so those three files must exist in the same folder (even as simple
 * placeholders) or the compiler will report "cannot find symbol".
 * ---------------------------------------------------------------
 */