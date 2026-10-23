import java.awt.*;
import java.awt.event.*;

/**
 * AdminDashboard.java
 * Election Voting Mechanism System - Admin Dashboard (Java AWT only).
 *
 * All election data comes from the shared classes:
 *   VotingData - candidates, election status, votes
 *   VoterData  - registered voters (read through VotingData)
 * This screen keeps NO lists of its own; it only displays and changes the
 * shared data.
 *
 * View Results -> opens ResultsPage   |   Logout -> returns to MainMenu
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class AdminDashboard extends Frame implements ActionListener {

    // ---------- Theme colours (same as the other screens) ----------
    private static final Color NAVY       = new Color(10, 36, 99);
    private static final Color GREEN      = new Color(40, 167, 69);
    private static final Color RED        = new Color(200, 35, 51);
    private static final Color LIGHT_GREY = new Color(238, 240, 243);
    private static final Color HEADER_ROW = new Color(228, 232, 238);
    private static final Color WHITE      = Color.WHITE;
    private static final Color TEXT_GREY  = new Color(90, 90, 90);

    // ---------- Theme fonts ----------
    private static final Font TITLE_FONT   = new Font("SansSerif", Font.BOLD, 22);
    private static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL_FONT   = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FIELD_FONT   = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font BUTTON_FONT  = new Font("SansSerif", Font.BOLD, 14);
    private static final Font SMALL_FONT   = new Font("SansSerif", Font.PLAIN, 12);
    // A fixed-width font keeps the candidate columns lined up in the List
    private static final Font LIST_FONT    = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font LIST_HEAD    = new Font("Monospaced", Font.BOLD, 14);

    // ---------- Maximum text lengths (keeps the columns tidy) ----------
    private static final int ID_MAX    = 10;
    private static final int NAME_MAX  = 24;
    private static final int PARTY_MAX = 20;

    // ---------- Duplicate-window protection ----------
    // Holds the one open AdminDashboard window (or null when none is open).
    private static AdminDashboard openInstance = null;

    // ---------- Components ----------
    private Label candidatesValue;   // summary panel numbers
    private Label votersValue;
    private Label votesValue;
    private Label statusValue;

    private List candidateList;      // java.awt.List showing the candidates

    private Button addButton;
    private Button removeButton;
    private Button startButton;
    private Button endButton;
    private Button resultsButton;
    private Button logoutButton;

    // True once we are leaving this screen; ignores extra clicks.
    private boolean leaving = false;

    /** Builds and shows the Admin Dashboard window. */
    public AdminDashboard() {
        super("Admin Dashboard");

        // If a dashboard is already open, bring it to the front
        // and do not create a second one.
        if (openInstance != null) {
            openInstance.toFront();
            dispose();
            return;
        }
        openInstance = this;

        setSize(940, 690);
        setResizable(false);
        setBackground(LIGHT_GREY);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);

        // Clicking the window's X button behaves like Logout
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        // Centre the window on the screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2,
                    (screen.height - getHeight()) / 2);

        refresh();          // fill the list and summary panels from VotingData
        setVisible(true);
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
    // SMALL HELPER PANEL
    // =====================================================================

    /** A Panel with inner padding (top, left, bottom, right). */
    private static class PaddedPanel extends Panel {
        private final Insets padding;

        PaddedPanel(LayoutManager layout, int top, int left, int bottom, int right) {
            super(layout);
            padding = new Insets(top, left, bottom, right);
        }

        @Override
        public Insets getInsets() {
            return padding;
        }
    }

    /** A thin coloured strip (used as an accent line). */
    private Panel createStrip(Color color, final int height) {
        Panel strip = new Panel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, height);
            }
        };
        strip.setBackground(color);
        return strip;
    }

    // =====================================================================
    // UI BUILDING METHODS
    // =====================================================================

    /** Navy header with the heading and a thin green accent line. */
    private Panel createHeaderPanel() {
        Panel header = new PaddedPanel(new GridLayout(2, 1, 0, 4), 18, 10, 14, 10);
        header.setBackground(NAVY);

        // \u2014 is the long dash; the escape avoids file-encoding problems
        Label title = new Label("Election Voting Mechanism System \u2014 Admin Dashboard", Label.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);

        Label subtitle = new Label("Logged in as Administrator", Label.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(144, 238, 144)); // light green

        header.add(title);
        header.add(subtitle);

        Panel wrapper = new Panel(new BorderLayout());
        wrapper.add(header, BorderLayout.CENTER);
        wrapper.add(createStrip(GREEN, 5), BorderLayout.SOUTH);
        return wrapper;
    }

    /** Everything under the header: summary panels, candidates, action buttons. */
    private Panel createBodyPanel() {
        Panel body = new PaddedPanel(new BorderLayout(0, 15), 20, 25, 20, 25);
        body.setBackground(LIGHT_GREY);

        body.add(createSummaryRow(), BorderLayout.NORTH);
        body.add(createCandidatesCard(), BorderLayout.CENTER);
        body.add(createActionRow(), BorderLayout.SOUTH);
        return body;
    }

    /** Four summary panels in one row. */
    private Panel createSummaryRow() {
        candidatesValue = new Label("0", Label.CENTER);
        votersValue     = new Label("0", Label.CENTER);
        votesValue      = new Label("0", Label.CENTER);
        statusValue     = new Label("Not Started", Label.CENTER);

        Panel row = new Panel(new GridLayout(1, 4, 15, 0));
        row.setBackground(LIGHT_GREY);
        row.add(createSummaryCard("Candidates", candidatesValue));
        row.add(createSummaryCard("Registered Voters", votersValue));
        row.add(createSummaryCard("Votes Cast", votesValue));
        row.add(createSummaryCard("Election Status", statusValue));
        return row;
    }

    /** One white summary panel: green strip, small title, big value. */
    private Panel createSummaryCard(String title, Label valueLabel) {
        Panel card = new Panel(new BorderLayout());
        card.setBackground(WHITE);

        Panel inner = new PaddedPanel(new GridLayout(2, 1, 0, 2), 10, 10, 12, 10);
        inner.setBackground(WHITE);

        Label titleLabel = new Label(title, Label.CENTER);
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(TEXT_GREY);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(NAVY);

        inner.add(titleLabel);
        inner.add(valueLabel);

        card.add(createStrip(GREEN, 4), BorderLayout.NORTH);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    /** White section with the candidate List and the Add / Remove buttons. */
    private Panel createCandidatesCard() {
        Panel card = new PaddedPanel(new BorderLayout(0, 8), 15, 20, 15, 20);
        card.setBackground(WHITE);

        // ----- heading + column titles -----
        Panel top = new Panel(new GridLayout(2, 1, 0, 6));
        top.setBackground(WHITE);

        Label heading = new Label("Candidates", Label.LEFT);
        heading.setFont(HEADING_FONT);
        heading.setForeground(NAVY);

        Label columns = new Label(formatRow("ID", "Name", "Party"), Label.LEFT);
        columns.setFont(LIST_HEAD);
        columns.setForeground(NAVY);
        columns.setBackground(HEADER_ROW);

        top.add(heading);
        top.add(columns);
        card.add(top, BorderLayout.NORTH);

        // ----- the candidate list -----
        candidateList = new List(8, false);   // 8 rows visible, single selection
        candidateList.setFont(LIST_FONT);
        card.add(candidateList, BorderLayout.CENTER);

        // ----- Add / Remove buttons and a hint -----
        addButton    = createButton("Add Candidate", GREEN, 150, 38);
        removeButton = createButton("Remove Selected Candidate", RED, 240, 38);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttons.setBackground(WHITE);
        buttons.add(addButton);
        buttons.add(removeButton);

        Label hint = new Label("Candidates can only be added or removed before the election starts.", Label.LEFT);
        hint.setFont(SMALL_FONT);
        hint.setForeground(TEXT_GREY);

        Panel bottom = new Panel(new GridLayout(2, 1, 0, 8));
        bottom.setBackground(WHITE);
        bottom.add(buttons);
        bottom.add(hint);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    /** Bottom row: Start, End, View Results and Logout. */
    private Panel createActionRow() {
        startButton   = createButton("Start Election", GREEN, 150, 44);
        endButton     = createButton("End Election", RED, 150, 44);
        resultsButton = createButton("View Results", NAVY, 150, 44);
        logoutButton  = createButton("Logout", TEXT_GREY, 150, 44);

        Panel row = new Panel(new GridLayout(1, 4, 12, 0));
        row.setBackground(LIGHT_GREY);
        row.add(startButton);
        row.add(endButton);
        row.add(resultsButton);
        row.add(logoutButton);
        return row;
    }

    /** Creates one styled button and attaches the ActionListener. */
    private Button createButton(String text, Color background, int width, int height) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(WHITE);
        button.setPreferredSize(new Dimension(width, height));
        button.addActionListener(this);
        return button;
    }

    // =====================================================================
    // SHOWING THE SHARED DATA
    // =====================================================================

    /**
     * Pads each value to a fixed width so the columns line up
     * (works because the List uses a fixed-width font).
     */
    private static String formatRow(String id, String name, String party) {
        return String.format(" %-" + ID_MAX + "s  %-" + NAME_MAX + "s  %-" + PARTY_MAX + "s",
                id, name, party);
    }

    /** Re-reads VotingData and updates the list and the four summary panels. */
    private void refresh() {
        // The list rows are in the same order as VotingData.getCandidates(),
        // so the selected index matches the candidate at that position.
        candidateList.removeAll();
        for (VotingData.Candidate c : VotingData.getCandidates()) {
            candidateList.add(formatRow(c.getId(), c.getName(), c.getParty()));
        }

        candidatesValue.setText(String.valueOf(VotingData.getCandidateCount()));
        votersValue.setText(String.valueOf(VotingData.getRegisteredVoterCount()));
        votesValue.setText(String.valueOf(VotingData.getTotalVotes()));

        VotingData.Status status = VotingData.getStatus();
        statusValue.setText(status.getLabel());
        if (status == VotingData.Status.RUNNING) {
            statusValue.setForeground(GREEN);
        } else if (status == VotingData.Status.ENDED) {
            statusValue.setForeground(RED);
        } else {
            statusValue.setForeground(NAVY);
        }
    }

    // =====================================================================
    // BUTTON ACTIONS
    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (leaving) {
            return; // already moving to another screen
        }

        Object source = e.getSource();

        if (source == addButton) {
            addCandidate();
        } else if (source == removeButton) {
            removeSelectedCandidate();
        } else if (source == startButton) {
            startElection();
        } else if (source == endButton) {
            endElection();
        } else if (source == resultsButton) {
            leaving = true;
            dispose();            // close the dashboard
            new ResultsPage();    // open the results screen
        } else if (source == logoutButton) {
            logout();
        }
    }

    // ---------------------------------------------------------------------
    // Add Candidate
    // ---------------------------------------------------------------------

    /** Opens a small form that collects the ID, name and party. */
    private void addCandidate() {
        // Candidates cannot change once voting has begun
        if (VotingData.getStatus() != VotingData.Status.NOT_STARTED) {
            showMessage("Not Allowed",
                    "Candidates can only be added\nbefore the election starts.", RED);
            return;
        }

        final Dialog dialog = new Dialog(this, "Add Candidate", true);
        dialog.setSize(470, 360);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());
        centerOnFrame(dialog);

        // ----- heading -----
        Label title = new Label("New Candidate", Label.CENTER);
        title.setFont(HEADING_FONT);
        title.setForeground(NAVY);
        Panel titlePanel = new PaddedPanel(new BorderLayout(), 20, 10, 0, 10);
        titlePanel.setBackground(WHITE);
        titlePanel.add(title, BorderLayout.CENTER);
        dialog.add(titlePanel, BorderLayout.NORTH);

        // ----- the three input rows -----
        final TextField idField    = createTextField();
        final TextField nameField  = createTextField();
        final TextField partyField = createTextField();

        Panel form = new PaddedPanel(new GridLayout(3, 1, 0, 14), 20, 30, 0, 30);
        form.setBackground(WHITE);
        form.add(createFormRow("Candidate ID", idField));
        form.add(createFormRow("Name", nameField));
        form.add(createFormRow("Party", partyField));
        dialog.add(form, BorderLayout.CENTER);

        // ----- error line (red) and buttons -----
        final Label errorLabel = new Label(" ", Label.CENTER);
        errorLabel.setFont(LABEL_FONT);
        errorLabel.setForeground(RED);

        Button saveButton   = createDialogButton("Save", GREEN);
        Button cancelButton = createDialogButton("Cancel", NAVY);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        Panel south = new Panel(new BorderLayout());
        south.setBackground(WHITE);
        south.add(errorLabel, BorderLayout.NORTH);
        south.add(buttonPanel, BorderLayout.CENTER);
        dialog.add(south, BorderLayout.SOUTH);

        // ----- Save: validate, then store in the shared VotingData -----
        ActionListener saveAction = ev -> {
            String id    = idField.getText().trim();
            String name  = nameField.getText().trim();
            String party = partyField.getText().trim();

            // Step 1: every field is required and must fit the columns
            if (id.isEmpty()) {
                errorLabel.setText("Please enter a Candidate ID.");
                idField.requestFocus();
                return;
            }
            if (id.length() > ID_MAX || id.matches(".*\\s.*")) {
                errorLabel.setText("ID: max " + ID_MAX + " characters, no spaces.");
                idField.requestFocus();
                return;
            }
            if (name.isEmpty()) {
                errorLabel.setText("Please enter the candidate's name.");
                nameField.requestFocus();
                return;
            }
            if (name.length() > NAME_MAX) {
                errorLabel.setText("Name can have at most " + NAME_MAX + " characters.");
                nameField.requestFocus();
                return;
            }
            if (party.isEmpty()) {
                errorLabel.setText("Please enter the party name.");
                partyField.requestFocus();
                return;
            }
            if (party.length() > PARTY_MAX) {
                errorLabel.setText("Party can have at most " + PARTY_MAX + " characters.");
                partyField.requestFocus();
                return;
            }

            // Step 2: the ID must be unique
            if (VotingData.candidateIdExists(id)) {
                errorLabel.setText("This Candidate ID already exists.");
                idField.requestFocus();
                return;
            }

            // Step 3: everything is valid, so save the candidate
            VotingData.addCandidate(new VotingData.Candidate(id, name, party));
            dialog.dispose();
            refresh();
            showMessage("Candidate Added",
                    "Candidate added successfully:\n" + name + " (" + party + ")", GREEN);
        };

        saveButton.addActionListener(saveAction);
        partyField.addActionListener(saveAction);                 // Enter in last box = Save
        cancelButton.addActionListener(ev -> dialog.dispose());

        // The dialog's X button behaves like Cancel
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);   // waits here until the dialog is closed
    }

    /** One text field for the Add Candidate form. */
    private TextField createTextField() {
        TextField field = new TextField();
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(240, 34));
        return field;
    }

    /** A fixed-width label on the left and the text field on the right. */
    private Panel createFormRow(String labelText, TextField field) {
        Panel row = new Panel(new BorderLayout(10, 0));
        row.setBackground(WHITE);

        Label label = new Label(labelText, Label.LEFT);
        label.setFont(LABEL_FONT);
        label.setForeground(NAVY);
        label.setPreferredSize(new Dimension(110, 34));

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /** A button for use inside dialogs. */
    private Button createDialogButton(String text, Color background) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(WHITE);
        button.setPreferredSize(new Dimension(110, 38));
        return button;
    }

    // ---------------------------------------------------------------------
    // Remove Selected Candidate
    // ---------------------------------------------------------------------

    private void removeSelectedCandidate() {
        // Candidates cannot change once voting has begun
        if (VotingData.getStatus() != VotingData.Status.NOT_STARTED) {
            showMessage("Not Allowed",
                    "Candidates can only be removed\nbefore the election starts.", RED);
            return;
        }

        // A candidate must be selected in the list
        int index = candidateList.getSelectedIndex();   // -1 means nothing selected
        if (index < 0) {
            showMessage("No Selection",
                    "Please select a candidate\nfrom the list first.", RED);
            return;
        }

        VotingData.Candidate candidate = VotingData.getCandidates().get(index);

        // Ask for confirmation before deleting
        boolean sure = confirm("Confirm Removal",
                "Remove this candidate?\n" + candidate.getName() + " (" + candidate.getId() + ")");
        if (!sure) {
            return;
        }

        VotingData.removeCandidate(candidate.getId());
        refresh();
        showMessage("Candidate Removed", candidate.getName() + " has been removed.", GREEN);
    }

    // ---------------------------------------------------------------------
    // Start Election / End Election
    // ---------------------------------------------------------------------

    private void startElection() {
        VotingData.Status status = VotingData.getStatus();

        if (status == VotingData.Status.RUNNING) {
            showMessage("Already Running", "The election is already running.", RED);
            return;
        }
        if (status == VotingData.Status.ENDED) {
            showMessage("Election Ended", "The election has already ended.", RED);
            return;
        }
        if (VotingData.getCandidateCount() < VotingData.MIN_CANDIDATES) {
            showMessage("Not Enough Candidates",
                    "At least " + VotingData.MIN_CANDIDATES + " candidates are required\nbefore the election can start.", RED);
            return;
        }

        boolean sure = confirm("Start Election",
                "Start the election now?\nCandidates can no longer be added or removed.");
        if (!sure) {
            return;
        }

        VotingData.startElection();
        refresh();
        showMessage("Election Started", "The election has started.\nVoters can now cast their votes.", GREEN);
    }

    private void endElection() {
        VotingData.Status status = VotingData.getStatus();

        if (status == VotingData.Status.NOT_STARTED) {
            showMessage("Not Started", "The election has not started yet.", RED);
            return;
        }
        if (status == VotingData.Status.ENDED) {
            showMessage("Already Ended", "The election has already ended.", RED);
            return;
        }

        boolean sure = confirm("End Election",
                "End the election now?\nVoting will close and cannot be reopened.");
        if (!sure) {
            return;
        }

        VotingData.endElection();
        refresh();
        showMessage("Election Ended", "The election has ended.\nYou can now view the results.", GREEN);
    }

    // ---------------------------------------------------------------------
    // Logout
    // ---------------------------------------------------------------------

    /** Closes the dashboard and returns to the main menu (once only). */
    private void logout() {
        if (leaving) {
            return;
        }
        leaving = true;
        dispose();
        new MainMenu();
    }

    // =====================================================================
    // MESSAGE AND CONFIRMATION DIALOGS
    // =====================================================================

    /** Moves a dialog so it sits in the middle of the dashboard window. */
    private void centerOnFrame(Dialog dialog) {
        dialog.setLocation(getX() + (getWidth() - dialog.getWidth()) / 2,
                           getY() + (getHeight() - dialog.getHeight()) / 2);
    }

    /** A panel with one Label per line of the message ("\n" = new line). */
    private Panel createMessagePanel(String message, Color color) {
        String[] lines = message.split("\n");
        Panel panel = new Panel(new GridLayout(lines.length, 1));
        panel.setBackground(WHITE);
        for (String line : lines) {
            Label lineLabel = new Label(line, Label.CENTER);
            lineLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            lineLabel.setForeground(color);
            panel.add(lineLabel);
        }
        return panel;
    }

    /**
     * AWT has no ready-made message box, so we build a small modal Dialog.
     * "Modal" means the dashboard cannot be used until it is closed.
     *
     * @param messageColor GREEN for success, RED for errors
     */
    private void showMessage(String title, String message, Color messageColor) {
        final Dialog dialog = new Dialog(this, title, true);
        dialog.setSize(440, 200);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());
        centerOnFrame(dialog);

        dialog.add(createMessagePanel(message, messageColor), BorderLayout.CENTER);

        Button okButton = createDialogButton("OK", NAVY);
        okButton.addActionListener(ev -> dialog.dispose());

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);   // waits here until the dialog is closed
    }

    /**
     * Shows a Yes / No question and returns true only if the user clicks Yes.
     * Closing the dialog with the X counts as No.
     */
    private boolean confirm(String title, String message) {
        // A one-item array lets the button code (below) change the answer
        final boolean[] answer = { false };

        final Dialog dialog = new Dialog(this, title, true);
        dialog.setSize(440, 210);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());
        centerOnFrame(dialog);

        dialog.add(createMessagePanel(message, NAVY), BorderLayout.CENTER);

        Button yesButton = createDialogButton("Yes", GREEN);
        Button noButton  = createDialogButton("No", NAVY);

        yesButton.addActionListener(ev -> {
            answer[0] = true;
            dialog.dispose();
        });
        noButton.addActionListener(ev -> dialog.dispose());

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);   // waits here until the dialog is closed
        return answer[0];
    }

    // =====================================================================
    // PROGRAM ENTRY POINT (lets you test this screen on its own)
    // =====================================================================

    public static void main(String[] args) {
        new AdminDashboard();
    }
}

/*
 * ---------------------------------------------------------------
 * HOW TO COMPILE AND RUN (VS Code terminal, inside the src folder)
 * ---------------------------------------------------------------
 *   cd src
 *   Remove-Item *.class     <- (PowerShell) clears old compiled files
 *   javac *.java            <- compiles all screens together
 *   java MainMenu           <- normal start: Admin Login (admin / 1234)
 *
 * AdminDashboard needs VotingData.java, VoterData.java and
 * ResultsPage.java in the same folder.
 * ---------------------------------------------------------------
 */