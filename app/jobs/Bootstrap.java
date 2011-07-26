package jobs;

import play.Play;
import play.Play.Mode;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
        if(Play.mode == Mode.DEV) {
            System.out.println("Bootstrap.doJob() DEV mode bootstrap");
            if(Play.id.equals("test")) {
                System.out.println("Bootstrap.doJob() TEST mode bootstrap");
                Fixtures.loadModels("data.yml");
                System.out.println("Bootstrap.doJob() data.yml loaded");
                Fixtures.loadModels("inventory.yml");
                System.out.println("Bootstrap.doJob() inventory.yml loaded");
            }
        }
    }
}
