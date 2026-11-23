import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class TransferBank extends MetodePembayaran {
    private String nomorRekening;

    public TransferBank(String namaMetode, String nomorRekening) {
        super(namaMetode);
        this.nomorRekening = nomorRekening;
    }

    protected static String formatRupiah(double nominal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return "Rp " + df.format((long) nominal);
    }

    @Override
    public String prosesPembayaran(double jumlah) {
        return String.join("\n",
            "Metode        : Transfer " + getNamaMetode(),
            "No. Rekening  : " + nomorRekening,
            "Subtotal      : " + formatRupiah(jumlah),
            "Diskon        : Rp 0",
            "Total Bayar   : " + formatRupiah(jumlah),
            "Status        : LUNAS / BERHASIL!"
        );
    }
}