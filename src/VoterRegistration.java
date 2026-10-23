import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * VoterRegistration.java
 * Election Voting Mechanism System - Voter Registration screen (Java AWT only).
 *
 * Registered voters are saved in VoterData (in memory only, lost when the
 * program closes). Back -> returns to MainMenu.
 *
 * The voter gives a Date of Birth (DD/MM/YYYY, must be 18+) and a 12-digit
 * Aadhaar number. The Aadhaar number is hidden while typing and is never
 * shown in any message.
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class VoterRegistration extends Frame implements ActionListener {

    // ---------- Theme colours (same as MainMenu and AdminLogin) ----------
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

    // Minimum and maximum ages accepted by this prototype
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 120;

    // Aadhaar numbers have exactly this many digits
    private static final int AADHAAR_LENGTH = 12;

    // Strict DD/MM/YYYY: rejects 31/02/2000, 1/1/2000, 2000-01-01, etc.
    private static final DateTimeFormatter DOB_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    // ---------- Duplicate-window protection ----------
    // Holds the one open VoterRegistration window (or null when none is open).
    private static VoterRegistration openInstance = null;

    // ---------- Components ----------
    private TextField nameField;
    private TextField dobField;
    private TextField aadhaarField;
    private TextField voterIdField;
    private TextField usernameField;
    private TextField passwordField;
    private Button registerButton;
    private Button backButton;

    // True once we are leaving this screen; ignores extra clicks.
    private boolean leaving = false;

    /** Builds and shows the Voter Registration window. */
    public VoterRegistration() {
        super("Voter Registration");

        // If a registration window is already open, bring it to the front
        // and do not create a second one.
        if (openInstance != null) {
            openInstance.toFront();
            dispose();
            return;
        }
        openInstance = this;

        setSize(560, 640);
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
        nameField.requestFocus(); // cursor starts in the first box
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
                return new Insets(26, 10, 20, 10);
            }
        };
        header.setBackground(NAVY);

        Label title = new Label("Voter Registration", Label.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);

        Label subtitle = new Label("Create your voter account", Label.CENTER);
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

    /** Light-grey area holding the white registration form card. */
    private Panel createCenterPanel() {
        // GridBagLayout with no constraints keeps the card centred
        Panel center = new Panel(new GridBagLayout());
        center.setBackground(LIGHT_GREY);

        // White card with inner padding
        Panel card = new Panel(new BorderLayout(0, 26)) {
            @Override
            public Insets getInsets() {
                return new Insets(30, 40, 30, 40);
            }
        };
        card.setBackground(WHITE);

        // ----- The six input fields -----
        nameField     = createTextField();
        dobField      = createTextField();
        aadhaarField  = createTextField();
        aadhaarField.setEchoChar('*');           // hides the Aadhaar number while typing
        voterIdField  = createTextField();
        usernameField = createTextField();
        passwordField = createTextField();
        passwordField.setEchoChar('*');          // masks the typed password
        passwordField.addActionListener(this);   // Enter in the last box = Register

        // One row per field, stacked vertically
        Panel fields = new Panel(new GridLayout(6, 1, 0, 14));
        fields.setBackground(WHITE);
        fields.add(createFormRow("Full Name", nameField));
        fields.add(createFormRow("DOB (DD/MM/YYYY)", dobField));
        fields.add(createFormRow("Aadhaar Number", aadhaarField));
        fields.add(createFormRow("Voter ID", voterIdField));
        fields.add(createFormRow("Username", usernameField));
        fields.add(createFormRow("Password", passwordField));
        card.add(fields, BorderLayout.CENTER);

        // ----- Back and Register buttons side by side -----
        backButton     = createButton("Back", NAVY);
        registerButton = createButton("Register", GREEN);

        Panel buttons = new Panel(new GridLayout(1, 2, 15, 0));
        buttons.setBackground(WHITE);
        buttons.add(backButton);
        buttons.add(registerButton);
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

    /** One text field with a fixed size. */
    private TextField createTextField() {
        TextField field = new TextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(260, 34));
        return field;
    }

    /**
     * One form row: a fixed-width label on the left and the text field on the
     * right. The fixed label width keeps all fields neatly aligned.
     */
    private Panel createFormRow(String labelText, TextField field) {
        Panel row = new Panel(new BorderLayout(10, 0));
        row.setBackground(WHITE);

        Label label = new Label(labelText, Label.LEFT);
        label.setFont(LABEL_FONT);
        label.setForeground(NAVY);
        label.setPreferredSize(new Dimension(150, 34));

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
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
            // Register button, or Enter pressed in the password box
            registerVoter();
        }
    }

    /** Validates the form and, if everything is correct, saves the voter. */
    private void registerVoter() {
        if (leaving) {
            return; // already moving to another screen
        }

        // Read all inputs. trim() removes spaces at the start and end.
        String fullName = nameField.getText().trim();
        String dobText  = dobField.getText().trim();
        String aadhaar  = aadhaarField.getText().trim();
        String voterId  = voterIdField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // ----- Step 1: no required field may be empty -----
        if (isEmpty(fullName, nameField, "Full Name"))     return;
        if (isEmpty(dobText, dobField, "Date of Birth"))   return;
        if (isEmpty(aadhaar, aadhaarField, "Aadhaar Number")) return;
        if (isEmpty(voterId, voterIdField, "Voter ID"))    return;
        if (isEmpty(username, usernameField, "Username"))  return;
        if (isEmpty(password, passwordField, "Password"))  return;

        // ----- Step 2: Date of Birth must be a real DD/MM/YYYY date, 18 or older -----
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobText, DOB_FORMAT);   // fails if not a real DD/MM/YYYY date
        } catch (DateTimeParseException ex) {
            showMessage("Invalid Date of Birth",
                    "Enter your date of birth as DD/MM/YYYY\n(for example 25/12/2000).", RED);
            dobField.requestFocus();
            return;
        }

        LocalDate today = LocalDate.now();
        if (dob.isAfter(today)) {
            showMessage("Invalid Date of Birth",
                    "Date of birth cannot be in the future.", RED);
            dobField.requestFocus();
            return;
        }

        int age = Period.between(dob, today).getYears();   // exact age in whole years
        if (age < MIN_AGE) {
            showMessage("Not Eligible",
                    "You must be at least " + MIN_AGE + " years old\nto register as a voter.", RED);
            dobField.requestFocus();
            return;
        }
        if (age > MAX_AGE) {
            showMessage("Invalid Date of Birth",
                    "Please enter a realistic date of birth.", RED);
            dobField.requestFocus();
            return;
        }

        // ----- Step 2b: Aadhaar must be exactly 12 digits -----
        // (the number itself is never shown in any message)
        if (!isValidAadhaar(aadhaar)) {
            showMessage("Invalid Aadhaar Number",
                    "Aadhaar number must contain exactly " + AADHAAR_LENGTH + " digits\n"
                    + "(digits only, no spaces or letters).", RED);
            aadhaarField.requestFocus();
            return;
        }

        // ----- Step 3: Voter ID and username must be unique -----
        if (VoterData.voterIdExists(voterId)) {
            showMessage("Duplicate Voter ID",
                    "This Voter ID is already registered.", RED);
            voterIdField.requestFocus();
            return;
        }
        if (VoterData.aadhaarExists(aadhaar)) {
            showMessage("Already Registered",
                    "A voter with this Aadhaar number\nis already registered.", RED);
            aadhaarField.requestFocus();
            return;
        }
        if (VoterData.usernameExists(username)) {
            showMessage("Username Taken",
                    "This username is already taken.\nPlease choose another one.", RED);
            usernameField.requestFocus();
            return;
        }

        // ----- Step 4: everything is valid, so save the voter -----
        VoterData.addVoter(new VoterData.Voter(fullName, dob, aadhaar, voterId, username, password));

        showMessage("Registration Successful",
                "Registration successful!\nYou can now log in from the main menu.", GREEN);

        clearFields();
        nameField.requestFocus();
    }

    /**
     * Helper: returns true (and shows an error) if the text is empty,
     * and puts the cursor in the field that needs attention.
     */
    private boolean isEmpty(String text, TextField field, String fieldName) {
        if (text.isEmpty()) {
            showMessage("Missing Information", "Please enter your " + fieldName + ".", RED);
            field.requestFocus();
            return true;
        }
        return false;
    }

    /** True if the text is exactly 12 characters and every one is a digit 0-9. */
    private boolean isValidAadhaar(String text) {
        if (text.length() != AADHAAR_LENGTH) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /** Empties every input box. */
    private void clearFields() {
        nameField.setText("");
        dobField.setText("");
        aadhaarField.setText("");
        voterIdField.setText("");
        usernameField.setText("");
        passwordField.setText("");
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
     * Use "\n" inside the message to start a new line.
     *
     * @param title        text in the dialog's title bar
     * @param message      the message shown to the user
     * @param messageColor GREEN for success, RED for errors
     */
    private void showMessage(String title, String message, Color messageColor) {
        final Dialog dialog = new Dialog(this, title, true);
        dialog.setSize(420, 190);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());

        // Centre the dialog over the registration window
        dialog.setLocation(getX() + (getWidth() - 420) / 2,
                           getY() + (getHeight() - 190) / 2);

        // One Label per line of the message
        String[] lines = message.split("\n");
        Panel messagePanel = new Panel(new GridLayout(lines.length, 1));
        messagePanel.setBackground(WHITE);
        for (String line : lines) {
            Label lineLabel = new Label(line, Label.CENTER);
            lineLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            lineLabel.setForeground(messageColor);
            messagePanel.add(lineLabel);
        }
        dialog.add(messagePanel, BorderLayout.CENTER);

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
        new VoterRegistration();
    }
}

/*
 * ---------------------------------------------------------------
 * HOW TO COMPILE AND RUN (VS Code terminal, inside the src folder)
 * ---------------------------------------------------------------
 *   cd src
 *   javac *.java              <- compiles all screens together
 *   java MainMenu             <- normal start (menu -> Voter Registration)
 *   java VoterRegistration    <- or test this screen on its own
 *
 * VoterRegistration needs VoterData.java in the same folder.
 * Registered voters exist only while the program is running.
 * ---------------------------------------------------------------
 */