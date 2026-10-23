import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ResultsPage.java
 * Election Voting Mechanism System - Election Results screen (Java AWT only).
 *
 * All data comes from the shared VotingData class:
 *   VotingData.getCandidates()   VotingData.getVotesFor(id)
 *   VotingData.getTotalVotes()   VotingData.getStatus()
 *
 * Refresh Results      -> re-reads VotingData and redraws the results
 * Back to Dashboard    -> returns to AdminDashboard
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class ResultsPage extends Frame implements ActionListener {

    // ---------- Theme colours (same as the other screens) ----------
    private static final Color NAVY       = new Color(10, 36, 99);
    private static final Color GREEN      = new Color(40, 167, 69);
    private static final Color LIGHT_GREY = new Color(238, 240, 243);
    private static final Color HEADER_ROW = new Color(228, 232, 238);
    private static final Color ALT_ROW    = new Color(246, 248, 250);
    private static final Color LEADER_ROW = new Color(226, 246, 231);
    private static final Color WARN_BG    = new Color(255, 243, 205);
    private static final Color WARN_TEXT  = new Color(133, 100, 4);
    private static final Color WHITE      = Color.WHITE;
    private static final Color TEXT_GREY  = new Color(90, 90, 90);

    // ---------- Theme fonts ----------
    private static final Font TITLE_FONT    = new Font("SansSerif", Font.BOLD, 26);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font BANNER_FONT   = new Font("SansSerif", Font.BOLD, 14);
    private static final Font TABLE_HEAD    = new Font("SansSerif", Font.BOLD, 14);
    private static final Font TABLE_FONT    = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font RESULT_FONT   = new Font("SansSerif", Font.BOLD, 18);
    private static final Font BUTTON_FONT   = new Font("SansSerif", Font.BOLD, 15);
    private static final Font SMALL_FONT    = new Font("SansSerif", Font.PLAIN, 11);

    // ---------- Duplicate-window protection ----------
    private static ResultsPage openInstance = null;

    // ---------- Components ----------
    private Panel resultsCard;        // white card, rebuilt on every refresh
    private Label refreshedLabel;     // "Last refreshed at ..."
    private Button refreshButton;
    private Button backButton;

    // True once we are leaving this screen; ignores extra clicks.
    private boolean leaving = false;

    /** One table row: a candidate together with their vote count. */
    private static class Row {
        final VotingData.Candidate candidate;
        final int votes;

        Row(VotingData.Candidate candidate, int votes) {
            this.candidate = candidate;
            this.votes = votes;
        }
    }

    /** Builds and shows the Results window. */
    public ResultsPage() {
        super("Election Results");

        if (openInstance != null) {
            openInstance.toFront();
            dispose();
            return;
        }
        openInstance = this;

        setSize(780, 680);
        setResizable(false);
        setBackground(LIGHT_GREY);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshResults(); // fill the card with the current data

        // The X button behaves like Back to Dashboard
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goBack();
            }
        });

        // Centre the window on the screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2,
                    (screen.height - getHeight()) / 2);

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
    // UI BUILDING METHODS
    // =====================================================================

    /** Navy header with title, subtitle and a thin green accent line. */
    private Panel createHeaderPanel() {
        Panel header = new Panel(new GridLayout(2, 1, 0, 6)) {
            @Override
            public Insets getInsets() {
                return new Insets(24, 10, 20, 10);
            }
        };
        header.setBackground(NAVY);

        Label title = new Label("Election Voting Mechanism System", Label.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);

        Label subtitle = new Label("Election Results", Label.CENTER);
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(new Color(144, 238, 144)); // light green

        header.add(title);
        header.add(subtitle);

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

    /** Light-grey area that holds the white results card. */
    private Panel createBodyPanel() {
        Panel body = new Panel(new BorderLayout()) {
            @Override
            public Insets getInsets() {
                return new Insets(20, 25, 5, 25);
            }
        };
        body.setBackground(LIGHT_GREY);

        resultsCard = new Panel(new BorderLayout(0, 12)) {
            @Override
            public Insets getInsets() {
                return new Insets(18, 20, 18, 20);
            }
        };
        resultsCard.setBackground(WHITE);

        body.add(resultsCard, BorderLayout.CENTER);
        return body;
    }

    /** Refresh / Back buttons plus a small "last refreshed" note. */
    private Panel createButtonPanel() {
        refreshButton = createButton("Refresh Results", GREEN);
        backButton    = createButton("Back to Dashboard", NAVY);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        buttons.setBackground(LIGHT_GREY);
        buttons.add(refreshButton);
        buttons.add(backButton);

        refreshedLabel = new Label("", Label.CENTER);
        refreshedLabel.setFont(SMALL_FONT);
        refreshedLabel.setForeground(TEXT_GREY);

        Panel south = new Panel(new BorderLayout());
        south.setBackground(LIGHT_GREY);
        south.add(buttons, BorderLayout.CENTER);
        south.add(refreshedLabel, BorderLayout.SOUTH);
        return south;
    }

    private Button createButton(String text, Color background) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(WHITE);
        button.setPreferredSize(new Dimension(200, 44));
        button.addActionListener(this);
        return button;
    }

    // =====================================================================
    // REFRESH - reads VotingData again and rebuilds the card
    // =====================================================================

    private void refreshResults() {
        // ----- 1. Read the current data from the shared class -----
        VotingData.Status status = VotingData.getStatus();
        int totalVotes = VotingData.getTotalVotes();

        List<Row> rows = new ArrayList<>();
        for (VotingData.Candidate c : VotingData.getCandidates()) {
            rows.add(new Row(c, VotingData.getVotesFor(c.getId())));
        }
        // Most votes first (the sort is stable, so equal votes keep their order)
        rows.sort((a, b) -> Integer.compare(b.votes, a.votes));

        // ----- 2. Rebuild the card -----
        resultsCard.removeAll();
        resultsCard.add(createStatusBanner(status), BorderLayout.NORTH);
        resultsCard.add(createTable(rows, totalVotes), BorderLayout.CENTER);
        resultsCard.add(createSummaryPanel(rows, totalVotes, status), BorderLayout.SOUTH);
        resultsCard.validate();

        refreshedLabel.setText("Last refreshed at "
                + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        validate();
    }

    /** Coloured message at the top: provisional / not started / final. */
    private Panel createStatusBanner(VotingData.Status status) {
        String text;
        Color background;
        Color foreground;

        if (status == VotingData.Status.RUNNING) {
            text = "The election is still active - these results are provisional.";
            background = WARN_BG;
            foreground = WARN_TEXT;
        } else if (status == VotingData.Status.NOT_STARTED) {
            text = "The election has not started yet.";
            background = HEADER_ROW;
            foreground = NAVY;
        } else {
            text = "The election has ended - these are the final results.";
            background = LEADER_ROW;
            foreground = new Color(21, 107, 41);
        }

        Label label = new Label(text, Label.CENTER);
        label.setFont(BANNER_FONT);
        label.setForeground(foreground);

        Panel banner = new Panel(new GridLayout(1, 1)) {
            @Override
            public Insets getInsets() {
                return new Insets(10, 10, 10, 10);
            }
        };
        banner.setBackground(background);
        banner.add(label);
        return banner;
    }

    /** Candidate table: Rank | Candidate | Party | Votes | Share. */
    private ScrollPane createTable(List<Row> rows, int totalVotes) {
        Panel table = new Panel(new GridBagLayout());
        table.setBackground(WHITE);

        addRow(table, 0, HEADER_ROW, NAVY, TABLE_HEAD,
                "Rank", "  Candidate", "  Party", "Votes", "Share");

        // Highest vote count (only meaningful if at least one vote exists)
        int topVotes = rows.isEmpty() ? 0 : rows.get(0).votes;

        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            int rank = computeRank(r.votes, rows);   // standard competition ranking

            boolean leader = totalVotes > 0 && r.votes == topVotes;
            Color background = leader ? LEADER_ROW : (i % 2 == 0 ? WHITE : ALT_ROW);
            Color foreground = leader ? new Color(21, 107, 41) : Color.BLACK;
            Font font = leader ? TABLE_HEAD : TABLE_FONT;

            addRow(table, i + 1, background, foreground, font,
                    String.valueOf(rank),
                    "  " + r.candidate.getName(),
                    "  " + r.candidate.getParty(),
                    String.valueOf(r.votes),
                    formatPercent(percentOf(r.votes, totalVotes)));
        }

        ScrollPane scroll = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scroll.setBackground(WHITE);
        scroll.add(table);
        return scroll;
    }

    /** Adds one row of five labels to the table. */
    private void addRow(Panel table, int rowIndex, Color background, Color foreground,
                        Font font, String rank, String name, String party,
                        String votes, String share) {
        String[] texts = { rank, name, party, votes, share };
        double[] weights = { 0.10, 0.34, 0.28, 0.13, 0.15 };
        int[] alignment = { Label.CENTER, Label.LEFT, Label.LEFT, Label.CENTER, Label.CENTER };

        for (int col = 0; col < texts.length; col++) {
            Label cell = new Label(texts[col], alignment[col]);
            cell.setFont(font);
            cell.setForeground(foreground);
            cell.setBackground(background);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = col;
            gbc.gridy = rowIndex;
            gbc.weightx = weights[col];
            gbc.fill = GridBagConstraints.BOTH;
            gbc.ipady = 10;
            table.add(cell, gbc);
        }
    }

    /** Total votes, then the winner / tie / "no votes" message. */
    private Panel createSummaryPanel(List<Row> rows, int totalVotes, VotingData.Status status) {
        Panel summary = new Panel(new GridLayout(3, 1, 0, 4)) {
            @Override
            public Insets getInsets() {
                return new Insets(10, 0, 0, 0);
            }
        };
        summary.setBackground(WHITE);

        Label totalLabel = new Label("Total votes cast: " + totalVotes, Label.CENTER);
        totalLabel.setFont(BANNER_FONT);
        totalLabel.setForeground(NAVY);

        String line1;
        String line2 = "";
        Color resultColor = GREEN;
        boolean ended = (status == VotingData.Status.ENDED);

        if (rows.isEmpty()) {
            line1 = "No candidates have been added yet.";
            resultColor = NAVY;
        } else if (totalVotes == 0) {
            line1 = "No votes have been cast yet.";
            line2 = "The winner will appear once voting begins.";
            resultColor = NAVY;
        } else {
            // Collect every candidate that has the highest vote count
            int topVotes = rows.get(0).votes;
            List<Row> leaders = new ArrayList<>();
            for (Row r : rows) {
                if (r.votes == topVotes) {
                    leaders.add(r);
                }
            }
            String percent = formatPercent(percentOf(topVotes, totalVotes));

            if (leaders.size() == 1) {
                Row w = leaders.get(0);
                line1 = (ended ? "Winner: " : "Currently leading: ")
                        + w.candidate.getName() + " (" + w.candidate.getParty() + ")";
                line2 = (ended ? "Winning percentage: " : "Current percentage: ")
                        + percent + "  (" + topVotes + " of " + pluralVotes(totalVotes) + ")";
            } else {
                String tieWord = (leaders.size() == 2) ? "between " : "among ";
                line1 = (ended ? "Result: Tie " + tieWord : "Currently tied: ")
                        + joinNames(leaders);
                // "each candidate" if everyone is tied, otherwise "each tied candidate"
                String who = (leaders.size() == rows.size())
                        ? "each candidate" : "each tied candidate";
                line2 = "No single winner - " + who + " received "
                        + pluralVotes(topVotes) + " (" + percent + " of all votes).";
                resultColor = NAVY;
            }
        }

        Label resultLabel = new Label(line1, Label.CENTER);
        resultLabel.setFont(RESULT_FONT);
        resultLabel.setForeground(resultColor);

        Label detailLabel = new Label(line2, Label.CENTER);
        detailLabel.setFont(BANNER_FONT);
        detailLabel.setForeground(TEXT_GREY);

        summary.add(totalLabel);
        summary.add(resultLabel);
        summary.add(detailLabel);
        return summary;
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    /** votes / total * 100, returning 0 when no votes exist (no division by zero). */
    private static double percentOf(int votes, int totalVotes) {
        if (totalVotes <= 0) {
            return 0.0;
        }
        return votes * 100.0 / totalVotes;
    }

    private static String formatPercent(double percent) {
        return String.format("%.1f%%", percent);
    }

    /**
     * Standard competition ranking ("1224"): rank = 1 + the number of
     * candidates with strictly MORE votes. Tied candidates share a rank and
     * the following rank(s) are skipped, e.g. votes 5, 5, 3, 0, 0 give
     * ranks 1, 1, 3, 4, 4. Candidates with zero votes share a rank too.
     */
    private static int computeRank(int votes, List<Row> rows) {
        int rank = 1;
        for (Row other : rows) {
            if (other.votes > votes) {
                rank++;
            }
        }
        return rank;
    }

    /** "1 vote" or "3 votes" (zero is plural: "0 votes"). */
    private static String pluralVotes(int count) {
        return count + (count == 1 ? " vote" : " votes");
    }

    /** "A", "A and B", or "A, B and C". */
    private static String joinNames(List<Row> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(i == list.size() - 1 ? " and " : ", ");
            }
            sb.append(list.get(i).candidate.getName());
        }
        return sb.toString();
    }

    // =====================================================================
    // BUTTON ACTIONS
    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == refreshButton) {
            if (!leaving) {
                refreshResults();
            }
        } else if (source == backButton) {
            goBack();
        }
    }

    /** Closes this window and returns to the admin dashboard (once only). */
    private void goBack() {
        if (leaving) {
            return;
        }
        leaving = true;
        dispose();
        new AdminDashboard();
    }

    // =====================================================================
    // PROGRAM ENTRY POINT
    // =====================================================================

    public static void main(String[] args) {
        new ResultsPage();
    }
}