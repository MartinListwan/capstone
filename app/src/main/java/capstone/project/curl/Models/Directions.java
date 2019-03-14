package capstone.project.curl.Models;

import java.util.ArrayList;
import java.util.List;

public class Directions {
    public boolean allDirectionsRetrieved = false;
    public List<String> directions;

    public Directions(boolean allDirectionsRetrieved, List<String> directions) {
        this.allDirectionsRetrieved = allDirectionsRetrieved;
        this.directions = directions;
    }
}
