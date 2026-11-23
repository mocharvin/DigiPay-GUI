public class EWallet extends MetodePembayaran implements Diskonable {
    private String nomorHP;

    public EWallet(String namaMetode, String nomorHP) {
        super(namaMetode);
        this.nomorHP = nomorHP;
    }

    @Override
    public double hitungDiskon(double jumlah) {
        return jumlah * 0.10; // diskon 10%
    }

    @Override
    public String prosesPembayaran(double jumlah) {
        double diskon = hitungDiskon(jumlah);
        double totalAkhir = jumlah - diskon;

        return String.join("\n",
            "Metode        : E-Wallet " + getNamaMetode(),
            "No. HP        : " + nomorHP,
            "Subtotal      : " + TransferBank.formatRupiah(jumlah),
            "Diskon (10%)  : -" + TransferBank.formatRupiah(diskon),
            "Total Bayar   : " + TransferBank.formatRupiah(totalAkhir),
            "Status        : LUNAS / BERHASIL!"
        );
    }
}