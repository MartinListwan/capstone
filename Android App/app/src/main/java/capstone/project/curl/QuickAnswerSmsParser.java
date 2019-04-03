package capstone.project.curl;
import java.util.ArrayList;
import java.util.List;

public class QuickAnswerSmsParser {

    public static QuickAnswerModel getQuickAnswrModel(String text){
        return new QuickAnswerModel(text);
    }

    public static class QuickAnswerModel{
        public boolean validData;
        public List<WebsiteQuickAnswer> websiteQuickAnswers = new ArrayList<>();
        public QuickAnswerModel(String textMessage){
            String [] deliminatedSourced ;
            try{
                deliminatedSourced = textMessage.split("\\|");

                for (String source : deliminatedSourced){
                    try{
                        if(source.contains("#")){
                            WebsiteQuickAnswer quickAnswer = new WebsiteQuickAnswer(source);
                            websiteQuickAnswers.add(quickAnswer);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e){

            }

            if (websiteQuickAnswers.size() != 0){
                validData = true;
            }
        }

        @Override
        public String toString() {
            return "QuickAnswerModel{" +
                    "validData=" + validData +
                    ", websiteQuickAnswers=" + websiteQuickAnswers +
                    '}';
        }

        public class WebsiteQuickAnswer {
            public String URL;
            public String message;
            public WebsiteQuickAnswer(String delimatedAnswer){
                String[] strings = delimatedAnswer.split("#");
                URL = strings[0];
                this.message = strings[1];
            }

            @Override
            public String toString() {
                return "WebsiteQuickAnswer{" +
                        "URL='" + URL + '\'' +
                        ", message='" + message + '\'' +
                        '}';
            }
        }
    }
}