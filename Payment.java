import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Payment extends JFrame {
    // Color Palette
    private static final Color BG_CREAM     = Color.decode("#F8F6E3");
    private static final Color MINT_SOFT    = Color.decode("#97E7E1");
    private static final Color CYAN_ACCENT  = Color.decode("#6AD4DD");
    private static final Color BLUE_PRIMARY = Color.decode("#7AA2E3");
    private static final Color CARD_SURFACE = new Color(255, 254, 249);
    private static final Color TEXT_DARK    = Color.decode("#1E293B");
    private static final Color TEXT_MUTED   = Color.decode("#64748B");
    private static final Color ERROR_COLOR  = Color.decode("#DC2626");

    // Data transaksi & opsi pembayaran
    private final String[] dataBank    = {"BankA", "BankB", "BankC", "BankD", "BankE"};
    private final String[] dataEWallet = {"Danu", "GoPal", "OvvO", "Wowo"};

    private boolean isEWallet = false;

    // Komponen UI
    private CardLayout cardLayout;
    private JPanel mainContainer;

    private JTextField txtTotalBelanja; // Input total tagihan yang bisa diisi sendiri
    private JButton btnTabBank;
    private JButton btnTabEWallet;
    private JLabel lblProvider;
    private JComboBox<String> cmbProvider;
    private JLabel lblNomor;
    private JTextField txtNomor;
    private JLabel lblPin;
    private JPasswordField txtPin;
    private JLabel lblError;

    private JLabel lblSubtotalVal;
    private JLabel lblDiskonVal;
    private JLabel lblTotalAkhirVal;
    private JTextArea txtStruk;

    public Payment() {
        setTitle("DigiPay - Sistem Pembayaran E-Commerce");
        setSize(450, 710); // Diperbesar dari 660 ke 710 agar tombol bayar tidak dempet
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(BG_CREAM);

        mainContainer.add(buatHalamanCheckout(), "CHECKOUT");
        mainContainer.add(buatHalamanLoading(), "LOADING");
        mainContainer.add(buatHalamanStruk(), "STRUK");

        add(mainContainer);
        pilihMetode(false); // Default ke Transfer Bank
    }

    private JPanel buatHalamanCheckout() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(BG_CREAM);
        page.setBorder(new EmptyBorder(20, 26, 20, 26));

        // 1. Header Brand
        JLabel lblBrand = new JLabel("DigiPay");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblBrand.setForeground(BLUE_PRIMARY);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Sistem Pembayaran E-Commerce");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(lblBrand);
        page.add(Box.createVerticalStrut(2));
        page.add(lblSub);
        page.add(Box.createVerticalStrut(14));

        // 2. Kartu Total Tagihan & Promo (Input Nominal Tagihan)
        RoundedPanel billCard = new RoundedPanel(16, MINT_SOFT, CYAN_ACCENT);
        billCard.setLayout(new BorderLayout(0, 4));
        billCard.setBorder(new EmptyBorder(12, 16, 12, 16));
        billCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        billCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTagihanTitle = new JLabel("Input Total Tagihan Belanja (Rp)");
        lblTagihanTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTagihanTitle.setForeground(TEXT_DARK);

        // Input field nominal tagihan belanja
        txtTotalBelanja = new JTextField("250000");
        txtTotalBelanja.setFont(new Font("SansSerif", Font.BOLD, 18));
        txtTotalBelanja.setForeground(TEXT_DARK);
        txtTotalBelanja.setBackground(Color.WHITE);
        txtTotalBelanja.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CYAN_ACCENT, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));

        // Live update harga saat pengguna mengetik angka
        txtTotalBelanja.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateRincianHarga(); }
            public void removeUpdate(DocumentEvent e) { updateRincianHarga(); }
            public void changedUpdate(DocumentEvent e) { updateRincianHarga(); }
        });

        JLabel lblPromo = new JLabel("Promo: Diskon 10% khusus pembayaran via E-Wallet");
        lblPromo.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPromo.setForeground(new Color(15, 76, 92));

        billCard.add(lblTagihanTitle, BorderLayout.NORTH);
        billCard.add(txtTotalBelanja, BorderLayout.CENTER);
        billCard.add(lblPromo, BorderLayout.SOUTH);

        page.add(billCard);
        page.add(Box.createVerticalStrut(14));

        // 3. Pilihan Metode Pembayaran (Segmented Toggle)
        JPanel tabPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tabPanel.setBackground(BG_CREAM);
        tabPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tabPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnTabBank = buatTombolTab("Transfer Bank");
        btnTabEWallet = buatTombolTab("E-Wallet (10% OFF)");

        btnTabBank.addActionListener(e -> pilihMetode(false));
        btnTabEWallet.addActionListener(e -> pilihMetode(true));

        tabPanel.add(btnTabBank);
        tabPanel.add(btnTabEWallet);

        page.add(tabPanel);
        page.add(Box.createVerticalStrut(12));

        // 4. Form Input Pembayaran
        RoundedPanel formCard = new RoundedPanel(16, CARD_SURFACE, MINT_SOFT);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(14, 16, 14, 16));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 235));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblProvider = buatLabelForm("Pilih Bank");
        cmbProvider = new JComboBox<>(dataBank);
        styleInput(cmbProvider);

        lblNomor = buatLabelForm("Nomor Rekening (10 digit angka)");
        txtNomor = new JTextField();
        styleInput(txtNomor);

        lblPin = buatLabelForm("PIN Bank (6 digit angka)");
        txtPin = new JPasswordField();
        styleInput(txtPin);

        lblError = new JLabel(" ");
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblError.setForeground(ERROR_COLOR);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lblProvider);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(cmbProvider);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(lblNomor);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(txtNomor);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(lblPin);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(txtPin);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(lblError);

        page.add(formCard);
        page.add(Box.createVerticalStrut(12));

        // 5. Ringkasan Rincian Harga
        RoundedPanel summaryCard = new RoundedPanel(16, CARD_SURFACE, MINT_SOFT);
        summaryCard.setLayout(new GridLayout(3, 2, 0, 4));
        summaryCard.setBorder(new EmptyBorder(10, 16, 10, 16));
        summaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSubtotalVal = buatNilaiRingkasan("Rp 0", false);
        lblDiskonVal = buatNilaiRingkasan("Rp 0", false);
        lblTotalAkhirVal = buatNilaiRingkasan("Rp 0", true);

        summaryCard.add(buatLabelRingkasan("Subtotal", false));
        summaryCard.add(lblSubtotalVal);
        summaryCard.add(buatLabelRingkasan("Diskon", false));
        summaryCard.add(lblDiskonVal);
        summaryCard.add(buatLabelRingkasan("Total Bayar", true));
        summaryCard.add(lblTotalAkhirVal);

        page.add(summaryCard);
        page.add(Box.createVerticalStrut(16));

        // 6. Tombol Bayar
        RoundedButton btnBayar = new RoundedButton("Bayar Sekarang", BLUE_PRIMARY, CYAN_ACCENT, Color.WHITE);
        btnBayar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnBayar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBayar.addActionListener(e -> prosesValidasiDanBayar());

        page.add(btnBayar);
        page.add(Box.createVerticalStrut(16)); // Memberi margin bawah agar tombol tidak dempet border

        return page;
    }

    private JPanel buatHalamanLoading() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(BG_CREAM);
        page.setBorder(new EmptyBorder(220, 40, 220, 40));

        JLabel lblJudul = new JLabel("Memverifikasi PIN...");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblJudul.setForeground(TEXT_DARK);
        lblJudul.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("Sedang memproses pembayaran DigiPay kamu");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc.setForeground(TEXT_MUTED);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setForeground(CYAN_ACCENT);
        progress.setBackground(MINT_SOFT);
        progress.setBorderPainted(false);
        progress.setMaximumSize(new Dimension(240, 8));
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);

        page.add(lblJudul);
        page.add(Box.createVerticalStrut(8));
        page.add(lblDesc);
        page.add(Box.createVerticalStrut(22));
        page.add(progress);
        return page;
    }

    private JPanel buatHalamanStruk() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(BG_CREAM);
        page.setBorder(new EmptyBorder(36, 28, 36, 28));

        // Badge Status Sukses
        RoundedPanel badge = new RoundedPanel(30, MINT_SOFT, CYAN_ACCENT);
        badge.setLayout(new GridBagLayout());
        badge.setMaximumSize(new Dimension(60, 60));
        badge.setPreferredSize(new Dimension(60, 60));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCheck = new JLabel("✓");
        lblCheck.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblCheck.setForeground(TEXT_DARK);
        badge.add(lblCheck);

        JLabel lblSukses = new JLabel("Pembayaran Berhasil");
        lblSukses.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblSukses.setForeground(TEXT_DARK);
        lblSukses.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Struk Transaksi DigiPay");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Kartu Struk
        RoundedPanel strukCard = new RoundedPanel(16, CARD_SURFACE, MINT_SOFT);
        strukCard.setLayout(new BorderLayout());
        strukCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        strukCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        strukCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtStruk = new JTextArea();
        txtStruk.setEditable(false);
        txtStruk.setOpaque(false);
        txtStruk.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtStruk.setForeground(TEXT_DARK);
        strukCard.add(txtStruk, BorderLayout.CENTER);

        RoundedButton btnSelesai = new RoundedButton("Transaksi Baru", BLUE_PRIMARY, CYAN_ACCENT, Color.WHITE);
        btnSelesai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnSelesai.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSelesai.addActionListener(e -> {
            txtNomor.setText("");
            txtPin.setText("");
            lblError.setText(" ");
            pilihMetode(false);
            cardLayout.show(mainContainer, "CHECKOUT");
        });

        page.add(badge);
        page.add(Box.createVerticalStrut(14));
        page.add(lblSukses);
        page.add(Box.createVerticalStrut(4));
        page.add(lblSub);
        page.add(Box.createVerticalStrut(24));
        page.add(strukCard);
        page.add(Box.createVerticalGlue());
        page.add(btnSelesai);

        return page;
    }

    private double getTotalBelanjaInput() {
        try {
            String text = txtTotalBelanja.getText().replaceAll("[^\\d]", "");
            if (text.isEmpty()) return 0;
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateRincianHarga() {
        double totalBelanja = getTotalBelanjaInput();
        lblSubtotalVal.setText(TransferBank.formatRupiah(totalBelanja));

        if (isEWallet) {
            Diskonable promo = new EWallet("", "");
            double diskon = promo.hitungDiskon(totalBelanja);
            lblDiskonVal.setText("-" + TransferBank.formatRupiah(diskon));
            lblDiskonVal.setForeground(new Color(13, 148, 136));
            lblTotalAkhirVal.setText(TransferBank.formatRupiah(totalBelanja - diskon));
        } else {
            lblDiskonVal.setText("Rp 0");
            lblDiskonVal.setForeground(TEXT_DARK);
            lblTotalAkhirVal.setText(TransferBank.formatRupiah(totalBelanja));
        }
    }

    private void pilihMetode(boolean ewallet) {
        this.isEWallet = ewallet;
        lblError.setText(" ");
        txtNomor.setText("");
        txtPin.setText("");

        if (ewallet) {
            btnTabEWallet.setBackground(CYAN_ACCENT);
            btnTabEWallet.setForeground(TEXT_DARK);
            btnTabBank.setBackground(CARD_SURFACE);
            btnTabBank.setForeground(TEXT_MUTED);

            lblProvider.setText("Pilih E-Wallet");
            cmbProvider.setModel(new DefaultComboBoxModel<>(dataEWallet));
            lblNomor.setText("Nomor Telepon / HP (12 digit angka)");
            lblPin.setText("PIN E-Wallet (6 digit angka)");
        } else {
            btnTabBank.setBackground(CYAN_ACCENT);
            btnTabBank.setForeground(TEXT_DARK);
            btnTabEWallet.setBackground(CARD_SURFACE);
            btnTabEWallet.setForeground(TEXT_MUTED);

            lblProvider.setText("Pilih Bank");
            cmbProvider.setModel(new DefaultComboBoxModel<>(dataBank));
            lblNomor.setText("Nomor Rekening (10 digit angka)");
            lblPin.setText("PIN Bank (6 digit angka)");
        }
        updateRincianHarga();
    }

    private void prosesValidasiDanBayar() {
        double totalBelanja = getTotalBelanjaInput();
        if (totalBelanja <= 0) {
            lblError.setText("Input error: Nominal total tagihan wajib diisi angka valid.");
            return;
        }

        String provider = (String) cmbProvider.getSelectedItem();
        String nomor = txtNomor.getText().trim();
        String pin = new String(txtPin.getPassword()).trim();

        if (!isEWallet) {
            if (!nomor.matches("\\d{10}")) {
                lblError.setText("Format salah: Nomor rekening wajib 10 digit angka.");
                return;
            }
            if (!pin.matches("\\d{6}")) {
                lblError.setText("Format salah: PIN Bank wajib 6 digit angka.");
                return;
            }
        } else {
            if (!nomor.matches("\\d{12}")) {
                lblError.setText("Format salah: Nomor HP wajib 12 digit angka.");
                return;
            }
            if (!pin.matches("\\d{6}")) {
                lblError.setText("Format salah: PIN E-Wallet wajib 6 digit angka.");
                return;
            }
        }

        lblError.setText(" ");

        // Polymorphism: instansiasi subclass ke variabel parent abstract class
        MetodePembayaran metodePilih = isEWallet
                ? new EWallet(provider, nomor)
                : new TransferBank(provider, nomor);

        // Tampilkan layar loading 1.5 detik tanpa membekukan UI
        cardLayout.show(mainContainer, "LOADING");
        Timer timer = new Timer(1500, e -> {
            String hasilStruk = metodePilih.prosesPembayaran(totalBelanja);
            txtStruk.setText(hasilStruk);
            cardLayout.show(mainContainer, "STRUK");
        });
        timer.setRepeats(false);
        timer.start();
    }

    // --- Helper Styling Komponen Minimalis ---

    private JLabel buatLabelForm(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleInput(JComponent comp) {
        comp.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comp.setForeground(TEXT_DARK);
        comp.setBackground(BG_CREAM);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (comp instanceof JTextField) {
            comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MINT_SOFT, 1, true),
                new EmptyBorder(4, 10, 4, 10)
            ));
        }
    }

    private JButton buatTombolTab(String teks) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(CYAN_ACCENT, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel buatLabelRingkasan(String teks, boolean bold) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, bold ? 14 : 12));
        lbl.setForeground(bold ? TEXT_DARK : TEXT_MUTED);
        return lbl;
    }

    private JLabel buatNilaiRingkasan(String teks, boolean bold) {
        JLabel lbl = new JLabel(teks, SwingConstants.RIGHT);
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, bold ? 14 : 12));
        lbl.setForeground(bold ? BLUE_PRIMARY : TEXT_DARK);
        return lbl;
    }

    // Custom Panel dengan sudut membulat (Rounded Corners)
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;
        private final Color borderColor;

        RoundedPanel(int radius, Color bgColor, Color borderColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Custom Button dengan efek hover halus
    static class RoundedButton extends JButton {
        private final Color normalColor;
        private final Color hoverColor;
        private Color currentColor;

        RoundedButton(String text, Color normal, Color hover, Color textColor) {
            super(text);
            this.normalColor = normal;
            this.hoverColor = hover;
            this.currentColor = normal;

            setFont(new Font("SansSerif", Font.BOLD, 14));
            setForeground(textColor);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    currentColor = hoverColor;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    currentColor = normalColor;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Payment().setVisible(true));
    }
}