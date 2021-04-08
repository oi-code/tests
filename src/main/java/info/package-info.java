@org.hibernate.annotations.GenericGenerator(
	name = "generator", 
	strategy = "enhanced-sequence", 
	parameters = {
		@org.hibernate.annotations.Parameter(name = "seqName", value = "JPWH_SEQUENCE"),
		@org.hibernate.annotations.Parameter(name = "initial_value", value = "1000") 
	})
package info;
