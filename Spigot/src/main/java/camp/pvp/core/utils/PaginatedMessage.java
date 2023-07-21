package camp.pvp.core.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PaginatedMessage {

    private String title;
    private List<String> entries;
    private int linesPerPage;

    public PaginatedMessage(String title, int linesPerPage) {
        this.title = title;
        this.entries = new ArrayList<>();
        this.linesPerPage = linesPerPage;
    }

    public String getPage(int page) {
        StringBuilder sb = new StringBuilder();
        int newPage = page - 1;

        if(page <= getMaxPages()) {
            String placeholderTitle = title
                    .replace("<page>", Integer.toString(page))
                    .replace("<pages>", Integer.toString(getMaxPages()));

            sb.append(placeholderTitle);
            for (int i = newPage * linesPerPage; i < (newPage + 1) * linesPerPage; i++) {
                if (i < entries.size()) {
                    sb.append("\n");
                    sb.append(entries.get(i));
                } else {
                    break;
                }
            }
        } else {
            return "&cPage not found.";
        }

        return Colors.get(sb.toString());
    }

    public int getMaxPages() {
        int i = 1;
        while(i * linesPerPage < entries.size()) {
            i++;
        }

        return i;
    }
}
