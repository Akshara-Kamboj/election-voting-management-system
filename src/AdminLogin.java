import java.awt.*;
import java.awt.event.*;

/**
 * AdminLogin.java
 * Election Voting Mechanism System - Admin Login screen (Java AWT only).
 *
 * Prototype credentials: username "admin", password "1234"
 * Success -> opens AdminDashboard   |   Back -> returns to MainMenu
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class AdminLogin extends Frame implements ActionListener {

    // ---------- Theme colours (same as MainMenu) ----------
    private static final Color NAVY       = new Color(10, 36, 99);
    private static final Color GREEN      = new Color(40, 167, 69);
    private static final Color RED        = new Color(200, 35, 51);
    private static final Color LIGHT_GREY = new Color(238, 240, 243);
    private static final Color WHITE      = Color.WHITE;
    private static final Color TEXT_GREY  = new Color(90, 90, 90);

    // ---------- Theme fonts ----------
    private static final Font TITLE_FONT    = new Font("SansSerif", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font LABEL_FONT    = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FIELD_FONT    = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font BUTTON_FONT   = new Font("SansSerif", Font.BOLD, 15);
    private static final Font SMALL_FONT    = new Font("SansSerif", Font.PLAIN, 11);

    // ---------- Temporary in-memory credentials (prototype only) ----------
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "1234";

    // ---------- Duplicate-window protection ----------
    // Holds the one open AdminLogin window (or null when none is open).
    private static AdminLogin openInstance = null;

    // ---------- Components ----------
    private TextField usernameField;
    private TextField passwordField;
    private Button loginButton;
    private Button backButton;

    // True once we are leaving this screen; ignores extra clicks.
    private boolean leaving = false;

    /** Builds and shows the Admin Login window. */
    public AdminLogin() {
        super("Admin Login");

        // If an Admin Login window is already open, bring it to the front
        // and do not create a second one.
        if (openInstance != null) {
            openInstance.toFront();
            dispose();
            return;
        }
        openInstance = this;

        setSize(520, 580);
        setResizable(false);
        setBackground(LIGHT_GREY);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Clicking the window's X button behaves like the Back button
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goBackToMenu();
            }
        });

        // Centre the window on the screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2,
                    (screen.height - getHeight()) / 2);

        setVisible(true);
        usernameField.requestFocus(); // cursor starts in the username box
    }

    /** Frees the "one window only" slot whenever this window is closed. */
    @Override
    public void dispose() {
        if (openInstance == this) {
            openInstance = null;
        }
        super.dispose();
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
                return new Insets(28, 10, 22, 10);
            }
        };
        header.setBackground(NAVY);

        Label title = new Label("Admin Login", Label.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);

        Label subtitle = new Label("Authorized personnel only", Label.CENTER);
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

        Panel wrapper = new Panel(new BorderLayout());
        wrapper.add(header, BorderLayout.CENTER);
        wrapper.add(accentLine, BorderLayout.SOUTH);
        return wrapper;
    }

    /** Light-grey area holding the white login form card. */
    private Panel createCenterPanel() {
        // GridBagLayout with no constraints keeps the card centred
        Panel center = new Panel(new GridBagLayout());
        center.setBackground(LIGHT_GREY);

        // White card with inner padding
        Panel card = new Panel(new BorderLayout(0, 28)) {
            @Override
            public Insets getInsets() {
                return new Insets(35, 40, 35, 40);
            }
        };
        card.setBackground(WHITE);

        // ----- Username and password fields -----
        usernameField = createTextField();
        passwordField = createTextField();
        passwordField.setEchoChar('*'); // masks the typed password

        Panel fields = new Panel(new GridLayout(2, 1, 0, 20));
        fields.setBackground(WHITE);
        fields.add(createFieldBlock("Username", usernameField));
        fields.add(createFieldBlock("Password", passwordField));
        card.add(fields, BorderLayout.CENTER);

        // ----- Back and Login buttons side by side -----
        backButton  = createButton("Back", NAVY);
        loginButton = createButton("Login", GREEN);

        Panel buttons = new Panel(new GridLayout(1, 2, 15, 0));
        buttons.setBackground(WHITE);
        buttons.add(backButton);
        buttons.add(loginButton);
        card.add(buttons, BorderLayout.SOUTH);

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

    /** One text field with a fixed size; pressing Enter triggers a login. */
    private TextField createTextField() {
        TextField field = new TextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(300, 34));
        field.addActionListener(this); // Enter key fires actionPerformed
        return field;
    }

    /** A label placed directly above its text field. */
    private Panel createFieldBlock(String labelText, TextField field) {
        Panel block = new Panel(new BorderLayout(0, 6));
        block.setBackground(WHITE);

        Label label = new Label(labelText, Label.LEFT);
        label.setFont(LABEL_FONT);
        label.setForeground(NAVY);

        block.add(label, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        return block;
    }

    /** Creates one styled button and attaches the ActionListener. */
    private Button createButton(String text, Color background) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(WHITE);
        button.setPreferredSize(new Dimension(130, 44));
        button.addActionListener(this);
        return button;
    }

    // =====================================================================
    // BUTTON ACTIONS
    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == backButton) {
            goBackToMenu();
        } else {
            // Login button, or Enter pressed inside either text field
            attemptLogin();
        }
    }

    /** Validates the input and checks the prototype credentials. */
    private void attemptLogin() {
        if (leaving) {
            return; // already moving to another screen
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Step 1: make sure both fields are filled in
        if (username.isEmpty()) {
            showMessage("Missing Username", "Please enter your username.", RED);
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showMessage("Missing Password", "Please enter your password.", RED);
            passwordField.requestFocus();
            return;
        }

        // Step 2: compare with the temporary in-memory credentials
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            showMessage("Login Successful", "Welcome, Administrator!", GREEN);

            leaving = true;        // block any further clicks
            dispose();             // close the login window
            new AdminDashboard();  // open the dashboard (must exist in src)
        } else {
            showMessage("Login Failed", "Incorrect username or password.", RED);
            passwordField.setText("");   // clear only the password
            passwordField.requestFocus();
        }
    }

    /** Closes this window and returns to the main menu (once only). */
    private void goBackToMenu() {
        if (leaving) {
            return;
        }
        leaving = true;
        dispose();
        new MainMenu();
    }

    // =====================================================================
    // MESSAGE DIALOG
    // =====================================================================

    /**
     * AWT has no ready-made message box, so we build a small modal Dialog.
     * "Modal" means this window cannot be used until the dialog is closed.
     *
     * @param title        text in the dialog's title bar
     * @param message      the message shown to the user
     * @param messageColor GREEN for success, RED for errors
     */
    private void showMessage(String title, String message, Color messageColor) {
        final Dialog dialog = new Dialog(this, title, true);
        dialog.setSize(380, 180);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());

        // Centre the dialog over the login window
        dialog.setLocation(getX() + (getWidth() - 380) / 2,
                           getY() + (getHeight() - 180) / 2);

        Label messageLabel = new Label(message, Label.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        messageLabel.setForeground(messageColor);
        dialog.add(messageLabel, BorderLayout.CENTER);

        Button okButton = new Button("OK");
        okButton.setFont(BUTTON_FONT);
        okButton.setBackground(NAVY);
        okButton.setForeground(WHITE);
        okButton.setPreferredSize(new Dimension(100, 36));
        okButton.addActionListener(ev -> dialog.dispose()); // OK closes the dialog

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // The dialog's X button also just closes the dialog
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true); // waits here until the dialog is closed
    }

    // =====================================================================
    // PROGRAM ENTRY POINT (lets you test this screen on its own)
    // =====================================================================

    public static void main(String[] args) {
        new AdminLogin();
    }
}

/*
 * ---------------------------------------------------------------
 * HOW TO COMPILE AND RUN (VS Code terminal, inside the src folder)
 * ---------------------------------------------------------------
 *   cd src
 *   javac *.java          <- compiles all screens together
 *   java MainMenu         <- normal start (menu -> Admin Login)
 *   java AdminLogin       <- or test this screen on its own
 *
 * Test login:  username = admin     password = 1234
 *
 * NOTE: AdminLogin opens AdminDashboard after a successful login, so
 * AdminDashboard.java must exist in the same folder or javac will say
 * "cannot find symbol".
 * ---------------------------------------------------------------
 */