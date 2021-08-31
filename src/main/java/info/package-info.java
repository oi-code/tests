@org.hibernate.annotations.GenericGenerator(
	name = "generator", 
	strategy = "enhanced-sequence", 
	parameters = {
		@org.hibernate.annotations.Parameter(name = "seqName", value = "JPWH_SEQUENCE"),
		@org.hibernate.annotations.Parameter(name = "initial_value", value = "11") 
	})
/*@org.hibernate.annotations.FetchProfiles({
    @FetchProfile(name="user2_profile",
	    fetchOverrides = @FetchProfile.FetchOverride(
		    entity=main.User2.class,
		    association="user",
		    mode=org.hibernate.annotations.FetchMode.JOIN))
	    
})*/
package info;

import org.hibernate.annotations.FetchProfile;
