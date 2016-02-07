package Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by hm94__000 on 04-Feb-16.
 */
public class App implements Parcelable{

    //Each app is one 'entry' within the JSON

    private String name; //im:name/label

    private String urlImSmall; //im:image/label[0]
    private String urlImMed; //im:image/label[1]
    private String urlImLarge; //im:image/label[2]

    private String summary; //summary/label

    private double price; //im:price/attributes/amount
    private String currency; //im:price/attributes/currency

    private String type; //im:contentType/attributes/label

    private String rights; //rights/label

    private String title; //title/label

    private String link; //link/attributes/href

    private String idLabel; //id/label
    private String idNumber; //id/attributes/im:id
    private String bundleId; //id/attributes/im:bundleId

    private String artist; //im:artist/label
    private String artistLink; //im:artist/attributes/href

    private String category; //category/attributes/label
    private String categoryId; //category/attributes/im:id
    private String scheme; //category/attributes/scheme

    private Date releaseDate; //im:releaseDate/attributes/label

    public App(String name, String urlImSmall, String urlImMed, String urlImLarge, String summary, double price, String currency, String type, String rights, String title, String link, String idLabel, String idNumber, String bundleId, String artist, String artistLink, String category, String categoryId, String scheme, Date releaseDate) {
        this.name = name;
        this.urlImSmall = urlImSmall;
        this.urlImMed = urlImMed;
        this.urlImLarge = urlImLarge;
        this.summary = summary;
        this.price = price;
        this.currency = currency;
        this.type = type;
        this.rights = rights;
        this.title = title;
        this.link = link;
        this.idLabel = idLabel;
        this.idNumber = idNumber;
        this.bundleId = bundleId;
        this.artist = artist;
        this.artistLink = artistLink;
        this.category = category;
        this.categoryId = categoryId;
        this.scheme = scheme;
        this.releaseDate = releaseDate;
    }

    public String getName() {
        return name;
    }

    public String getUrlImSmall() {
        return urlImSmall;
    }

    public String getUrlImMed() {
        return urlImMed;
    }

    public String getUrlImLarge() {
        return urlImLarge;
    }

    public String getSummary() {
        return summary;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getType() {
        return type;
    }

    public String getRights() {
        return rights;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getIdLabel() {
        return idLabel;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getBundleId() {
        return bundleId;
    }

    public String getArtist() {
        return artist;
    }

    public String getArtistLink() {
        return artistLink;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getScheme() {
        return scheme;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public App(Parcel in){
        name = in.readString();
        urlImSmall = in.readString();
        urlImMed = in.readString();
        urlImLarge = in.readString();
        summary = in.readString();
        currency = in.readString();
        type = in.readString();
        rights = in.readString();
        title = in.readString();
        link = in.readString();
        idLabel = in.readString();
        idNumber = in.readString();
        bundleId = in.readString();
        artist = in.readString();
        artistLink = in.readString();
        category = in.readString();
        categoryId = in.readString();
        scheme = in.readString();
        price = in.readDouble();
        releaseDate = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(urlImSmall);
        dest.writeString(urlImMed);
        dest.writeString(urlImLarge);
        dest.writeString(summary);
        dest.writeString(currency);
        dest.writeString(type);
        dest.writeString(rights);
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(idLabel);
        dest.writeString(idNumber);
        dest.writeString(bundleId);
        dest.writeString(artist);
        dest.writeString(artistLink);
        dest.writeString(category);
        dest.writeString(categoryId);
        dest.writeString(scheme);
        dest.writeDouble(price);
        dest.writeLong(releaseDate.getTime());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public App createFromParcel(Parcel in) {
            return new App(in);
        }
        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };
}
