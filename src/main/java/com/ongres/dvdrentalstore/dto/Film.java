package com.ongres.dvdrentalstore.dto;

public class Film
{
	private Actor actor;
	
	private String title;
	
	private String description;
	
	private String categoryName;

	public Actor getActor()
	{
		return actor;
	}

	public void setActor(Actor actor)
	{
		this.actor = actor;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	@Override
	public String toString()
	{
		return "Film [actor=" + actor + ", title=" + title + ", description=" + description + ", categoryName=" + categoryName + "]";
	}
}
