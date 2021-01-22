package io.github.u2ware.data.test;



public abstract class AbstractQueryBuilder {

	public abstract OrderBuilder orderBy() ;

	public abstract WhereBuilder where() ;
	
	public abstract Object build();

	
	public abstract static class OrderBuilder{
		
		public abstract OrderBuilder asc(String property);
		public abstract OrderBuilder desc(String property);
		
		public abstract Object build();
		
	}

	
	public abstract static class WhereBuilder{
		
		public abstract OrderBuilder orderBy() ;

		public abstract OperationBuilder<WhereBuilder> and() ;
		public abstract OperationBuilder<WhereBuilder> or() ;

		public abstract AndStartBuilder andStart() ;
		public abstract OrStartBuilder orStart() ;
		

		public abstract Object build();
	}
	
	public abstract static class AndStartBuilder {
		
		public abstract OperationBuilder<AndStartBuilder> and() ;
		public abstract OperationBuilder<AndStartBuilder> or() ;
		
		public abstract WhereBuilder andEnd();
		
	}
	public abstract static class OrStartBuilder {

		public abstract OperationBuilder<OrStartBuilder> and() ;
		public abstract OperationBuilder<OrStartBuilder> or() ;
		
		public abstract WhereBuilder orEnd();

	}

	public abstract static class OperationBuilder<B>{
		
		public abstract B eq(String p, Object value);
		public abstract B like(String p, Object value);
		
	}
	
	public void oops() throws Exception{
		AbstractQueryBuilder q = new AbstractQueryBuilder() {

			@Override
			public OrderBuilder orderBy() {
				return null;
			}

			@Override
			public WhereBuilder where() {
				return null;
			}

			@Override
			public Object build() {
				return null;
			}};
		
			
		q.build();
		
		q.orderBy().build();
		q.orderBy().asc("aaa").build();
		q.where().build();
		q.where().and().eq("1", "1").build();
		q.where().and().eq("1", "1").and().like("1", "1").build();

		q.where().andStart().and().eq("1", "1").and().like("1", "1").andEnd().build();
		q.where().orStart().and().eq("1", "1").and().like("1", "1").orEnd().build();
		q.where().orStart().and().eq("1", "1").and().like("1", "1").orEnd().orderBy().asc("1").build();
	
	}

}
