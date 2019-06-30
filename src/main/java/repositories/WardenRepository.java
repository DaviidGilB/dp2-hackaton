
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Charge;
import domain.Request;
import domain.TypeProduct;
import domain.Visit;
import domain.Visitor;
import domain.Warden;

@Repository
public interface WardenRepository extends JpaRepository<Warden, Integer> {

	@Query("select t from TypeProduct t, Product p where t = p.type")
	public List<TypeProduct> getProductTypesAssigned();

	@Query("select m from Warden m join m.userAccount u where u.username = ?1")
	public Warden getWardenByUsername(String username);

	@Query("select a from Charge a where a.titleEnglish = 'Suspicious'")
	public Charge getSuspiciousCharge();

	@Query("select v from Prisoner p join p.visits v where v.date > (NOW()) and p.id = ?1 and v.visitStatus != 'REJECTED'")
	public List<Visit> getFutureVisitsByPrisoner(int prisonerId);

	@Query("select r from Prisoner p join p.requests r join r.activity a where p.id = ?1 and r.status = 'APPROVED' and a.realizationDate > (NOW())")
	public List<Request> getRequestToFutureActivitiesByPrisoner(int prisonerId);

	/********** Statistics **********/

	/** Visitors and Prisoner with most Visits between them **/

	@Query("select v2 from Visitor v2 where (select max(cast((select count(v) from Visit v where v.visitor = v2 and v.prisoner = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))as integer)) from Prisoner p) = (select max(cast((select max(cast((select count(v) from Visit v where v.visitor = k and v.prisoner = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))as integer)) from Prisoner p)as integer)) from Visitor k)")
	public List<Visitor> getVisitorsMostVisitsToAPrisoner();

	/** Option 2 for getVisitorsMostVisitsToAPrisoner() **/

	@Query("select distinct d.visitor from Visit d where (select max(cast((select count(p) from Visit p where p.visitor = c.visitor and p.prisoner = c.prisoner and p.visitStatus = 'PERMITTED' and p.date < (NOW())) as integer)) from Visit c where c.visitStatus = 'PERMITTED' and c.date < (NOW())) = (select count(i) from Visit i where i.visitor = d.visitor and i.prisoner = d.prisoner and i.visitStatus = 'PERMITTED' and i.date < (NOW())) and d.visitStatus = 'PERMITTED' and d.date < (NOW())")
	public List<Visitor> getVisitorsMostVisitsToAPrisoner2();

	@Query("select j.userAccount.username from Prisoner j where (select max(cast((select count(v) from Visit v where v.visitor.id = ?1 and v.prisoner = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))as integer)) from Prisoner p) = (select count(v) from Visit v where v.visitor.id = ?1 and v.prisoner = j and v.visitStatus = 'PERMITTED' and v.date < (NOW()))")
	public List<String> getPrisonersWithMostVisitToAVisitor(int visitorId);

	/** Prisoners with visits to most different Visitors **/

	@Query("select a.userAccount.username from Prisoner a where a.freedom = false and (select count(distinct b) from Visit v  join v.visitor b where v.prisoner = a and v.visitStatus = 'PERMITTED' and v.date < (NOW())) = (select max(cast((select count(distinct b) from Visit v  join v.visitor b where v.prisoner = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))as integer)) from Prisoner p where p.freedom = false)")
	public List<String> getPrisonersWithVisitsToMostDifferentVisitors();

	/** Option 2 for getPrisonersWithVisitsToMostDifferentVisitors() **/
	@Query("select j.userAccount.username from Prisoner j where j.freedom = false and (select max(cast((select count( distinct v2) from Prisoner p join p.visits v join v.visitor v2 where a = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))as integer)) from Prisoner a where a.freedom = false) = (select count( distinct v2) from Prisoner p join p.visits v join v.visitor v2 where j = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))")
	public List<String> getPrisonersWithVisitsToMostDifferentVisitors2();

	/** Visitors with visits to most different Prisoners **/

	@Query("select a.userAccount.username from Visitor a where (select count(distinct b) from Visit v  join v.prisoner b where v.visitor = a and v.visitStatus = 'PERMITTED' and v.date < (NOW())) = (select max(cast((select count(distinct b) from Visit v  join v.prisoner b where v.visitor = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))as integer)) from Visitor p)")
	public List<String> getVisitorsWithVisitsToMostDifferentPrisoners();

	/** Option 2 for getVisitorsWithVisitsToMostDifferentPrisoners() **/
	@Query("select j.userAccount.username from Visitor j where (select max(cast((select count( distinct p2) from Visitor p join p.visits v join v.prisoner p2 where a = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))as integer)) from Visitor a) = (select count( distinct p2) from Visitor p join p.visits v join v.prisoner p2 where j = p and v.visitStatus = 'PERMITTED' and v.date < (NOW()))")
	public List<String> getVisitorsWithVisitsToMostDifferentPrisoners2();

	/** Ratio of carried out Visits with Report **/

	@Query("select round((select count(a)/cast(count(c) as float) from Visit a where a.report.id > 0)*100, 0) from Visit c where c.visitStatus = 'PERMITTED' and c.date < (NOW())")
	public Float getRatioOfVisitsWithReport();

	/** Guards with the largest number of Reports written **/

	@Query("select g3.userAccount.username from Guard g3 where (select count(v) from Guard g join g.visits v where g = g3 and v.report.id > 0) = (select max(cast((select count(v) from Guard g join g.visits v where g = g2 and v.report.id > 0) as integer)) from Guard g2)")
	public List<String> getGuardsWithTheLargestNumberOfReportsWritten();

	/** Guards with more than 50% of his Visits with a Report **/

	@Query("select round((select count(g3)/cast(count(g4)as float) from Guard g3 where ((select count(v) from Guard g join g.visits v where g = g3 and v.report.id > 0 and v.visitStatus = 'PERMITTED' and v.date < (NOW()))/(select count(v) from Guard g join g.visits v where g = g3 and v.visitStatus = 'PERMITTED' and v.date < (NOW()))) > 0.5)*100,2) from Guard g4")
	public Float getRatioOfGuardsWithMoreThan50PercentOfVisitsWithReport();

	/** Prisoners without a Visit in the last month **/

	@Query("select round((select count(p2)/cast(count(p3)as float) from Prisoner p2 where p2.freedom = false and (select count(v) from Prisoner p join p.visits v where p = p2 and v.visitStatus = 'PERMITTED' and v.date between (NOW() - 100000000) and (NOW())) = 0)*100, 2) from Prisoner p3 where p3.freedom = false")
	public Float getRatioOfPrisonersWithoutVisitsLastMonth();

	/** Visitors with at least 2 Visits to the same Prisoner in the last two months **/

	@Query("select round((select count(v)/cast(count(v3) as float) from Visitor v where (select count(distinct v2.prisoner) from Visit v2 where v2.visitor = v and v2.visitStatus = 'PERMITTED'and v2.date between (NOW() - 200000000) and (NOW())) < (select count(v2) from Visit v2 where v2.visitor = v and v2.visitStatus = 'PERMITTED'and v2.date between (NOW() - 200000000) and (NOW())))*100, 2) from Visitor v3")
	public Float getRegularVisitorToAtLeastOnePrisoner();

	/** Ratio of available Guards Vs Visits in need of a Guard **/

	@Query("select round(count(distinct g)/cast((select count(v2) from Visit v2 where (v2.visitStatus = 'PENDING' or v2.visitStatus = 'ACCEPTED') and v2.date between (NOW()) and (NOW() + 101000000))as float), 2) from Guard g join g.visits v where v.visitStatus = 'PERMITTED' and v.date between (NOW() - 100000000) and (NOW())")
	public Float getRatioOfAvailableGuardsVsFutureVisitsWithoutGuard();

	/** Top 3 prisoners with lowest crime rate **/

	@Query("select p.userAccount.username from Prisoner p where p.freedom = false order by(p.crimeRate)")
	public List<String> getTop3PrisonersLowestCrimeRate();

	/** Ratio of available Guards VS Visits in need of a Guard **/

	@Query("select (select count(p2)/count(p) from Prisoner p where p.isIsolated = true and p.freedom = false) from Prisoner p2 where p2.isIsolated = false and p2.freedom = false")
	public Float getRatioOfNonIsolatedVsIsolatedPrisoners();

	/** Maximum, minimun, standar deviation and average Crime Rate **/

	@Query("select max(p.crimeRate), min(p.crimeRate), round(stddev(p.crimeRate), 3), round(avg(p.crimeRate), 3) from Prisoner p")
	public Float[] getStatisticsCrimeRate();

	/** Social Workers with most Activities full filled **/

	@Query("select s.userAccount.username from SocialWorker s where (select count(a) from SocialWorker s2 join s2.activities a where s2 = s and a.maxAssistant = (select count(r) from Request r where r.activity = a and r.status = 'APPROVED')) = (select max(cast((select count(a2) from SocialWorker s3 join s3.activities a2 where s3 = s4 and a2.maxAssistant = (select count(r2) from Request r2 where r2.activity = a2 and r2.status = 'APPROVED'))as integer)) from SocialWorker s4)")
	public List<String> getSocialWorkerMostActivitiesFull();

	/** Prisoners with most Request to different Activities rejected and no accepted ones on those Activities **/

	@Query("select p2.userAccount.username from Prisoner p2 where (select count(distinct a) from Prisoner p join p.requests s join s.activity a where  p = p2 and s.status = 'REJECTED' and (select count(r) from Request r where r.prisoner = p and r.activity = a and r.status = 'APPROVED') = 0) > 0 and p2.freedom = false order by(cast((select count(distinct a) from Prisoner p join p.requests s join s.activity a where  p = p2 and s.status = 'REJECTED' and (select count(r) from Request r where r.prisoner = p and r.activity = a and r.status = 'APPROVED') = 0)as integer)) desc")
	public List<String> getPrisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities();

	/** Ratio of SocialWorkers with Curriculum **/

	@Query("select round((select count(s2)/cast(count(s)as float) from SocialWorker s2 where s2.curriculum.id > 0)*100, 0) from SocialWorker s")
	public Float getRatioSocialWorkersWithCurriculum();

	/** Activities with the largest number of Prisoners **/

	@Query("select a2.title from Activity a2 where a2.realizationDate > (NOW()) and (select count(distinct r.prisoner) from Request r where r.activity = a2 and r.status = 'APPROVED') = (select max(cast((select count(distinct r.prisoner) from Request r where r.activity = a and r.status = 'APPROVED')as integer)) from Activity a where a.realizationDate > (NOW()))")
	public List<String> getActivitiesLargestNumberPrisoners();

	/** Option 2 for getActivitiesLargestNumberPrisoners() **/

	@Query("select a2.title from Activity a2 where a2.realizationDate > (NOW()) and (select count(distinct r.prisoner) from Activity a join a.requests r where a.realizationDate > (NOW()) and a = a2 and r.status = 'APPROVED') = (select max(cast((select count(distinct r.prisoner) from Activity a join a.requests r where a = d  and a.realizationDate > (NOW()) and r.status = 'APPROVED')as integer)) from Activity d where d.realizationDate > (NOW()))")
	public List<String> getActivitiesLargestNumberPrisoners2();

	/** Activities with the largest average crime rate **/

	@Query("select a3.title from Activity a3 where (select round(avg(r2.prisoner.crimeRate),2) from Activity a4 join a4.requests r2 where a4 = a3 and r2.status = 'APPROVED') = (select (max(cast((select (round(avg(r.prisoner.crimeRate),2) + 1)*100 from Activity a join a.requests r where a = a2 and r.status = 'APPROVED')as float))/100) -1 from Activity a2)")
	public List<String> getActivitiesLargestAvgCrimeRate();

	/** Activities with the smallest average crime rate **/

	@Query("select a3.title from Activity a3 where (select round(avg(r2.prisoner.crimeRate),2) from Activity a4 join a4.requests r2 where a4 = a3 and r2.status = 'APPROVED') = (select (min(cast((select (round(avg(r.prisoner.crimeRate),2) + 1)*100 from Activity a join a.requests r where a = a2 and r.status = 'APPROVED')as float))/100) -1 from Activity a2)")
	public List<String> getActivitiesSmallestAvgCrimeRate();

	/** SocialWorkers with the lowest ratio of Prisoners per Activity **/

	@Query("select s3.userAccount.username from SocialWorker s3 where (select round((count(distinct r2)/cast(s4.activities.size as float))*100, 0) from SocialWorker s4 join s4.activities a2 join a2.requests r2 where s4 = s3 and r2.status = 'APPROVED') = (select min(cast((select round((count(distinct r)/cast(s.activities.size as float))*100, 0) from SocialWorker s join s.activities a join a.requests r where s = s2 and r.status = 'APPROVED')as integer)) from SocialWorker s2)")
	public List<String> getSocialWorkersLowestRatioPrisonersPerActivity();

	/** Activities who appears on most searches **/

	@Query("select a2.title from Activity a2 where (select count(f) from FinderActivities f where a2 member of f.activities) = (select max(cast((select count(f) from FinderActivities f where a member of f.activities)as integer)) from Activity a)")
	public List<String> getActivitiesMostSearched();

	/** Top 5 Prisoners who participated on most Activities last month **/

	@Query("select p2.userAccount.username from Prisoner p2 order by(cast((select count(r2) from Prisoner p3 join p3.requests r2 where p3 = p2 and r2.activity.realizationDate between (NOW() - 100000000) and (NOW())) as integer)) desc")
	public List<String> getTop5PrisonersParticipatedMostActivitiesLastMonth();

}
