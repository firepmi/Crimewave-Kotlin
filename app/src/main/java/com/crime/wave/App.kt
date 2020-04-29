package com.crime.wave

import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDexApplication
import com.squareup.picasso.Picasso


/**
 * Created by Mobile World on 4/8/2020.
 */
class App : MultiDexApplication() {
    var nexaBoldFont : Typeface? = null
    var nexaLightFont : Typeface? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        nexaBoldFont = ResourcesCompat.getFont(this, R.font.nexa_bold)
        nexaLightFont = ResourcesCompat.getFont(this, R.font.nexa_light)

        Picasso.get().isLoggingEnabled = true
    }

    companion object {
        var instance: App? = null
            private set
    }

    val key =
        "This-api-key-is-for-commercial-use-exclusively.Only-entities-with-a-Spotcrime-contract-May-use-this-key.Call-877.410.1607."
    val newsUrl =
        "https://newsapi.org/v2/top-headlines?apiKey=73373c784bd24a679fafc03522618936&"
    val gunMapUrl = "https://www.thetrace.org/features/gun-violence-interactive-shootings-map"
    val titleColors =
        arrayOf("#4a90e2", "#639c21", "#f8e71c", "#f5a623", "#e72929", "#6b0100")
    var crimeLevel = 0

    val levelTexts = arrayOf(
        "Minimal",
        "Low",
        "Moderate",
        "Elevated",
        "High",
        "Severely High"
    )
    val niceQueue = arrayListOf(
        "Happiness cannot be traveled to, owned, earned, worn or consumed. Happiness is the spiritual experience of living every minute with love, grace, and gratitude. - Denis Waitley",
        "Remember that the happiest people are not those getting more, but those giving more. - H. Jackson Brown, Jr.",
        "Nobody can give you freedom. Nobody can give you equality or justice or anything. If you're a man, you take it. - Malcolm X",
        "Happiness is not a matter of intensity but of balance, order, rhythm and harmony. - Thomas Merton",
        "The secret of happiness is to admire without desiring. - Carl Sandburg",
        "Happiness is nothing more than good health and a bad memory. - Albert Schweitzer",
        "Great minds discuss ideas; average minds discuss events; small minds discuss people. - Eleanor Roosevelt",
        "My only fault is that I don't realize how great I really am. - Muhammad Ali",
        "We are all faced with a series of great opportunities brilliantly disguised as impossible situations. - Charles R. Swindoll",
        "Behind every great man is a woman rolling her eyes. - Jim Carrey",
        "Pray that your loneliness may spur you into finding something to live for, great enough to die for. - Dag Hammarskjold",
        "I'd rather attempt to do something great and fail than to attempt to do nothing and succeed. - Robert H. Schuller",
        "The streets ain't made for everybody that’s why they made sidewalks.",
        "A battle lost or won is easily described, understood, and appreciated, but the moral growth of a great nation requires reflection, as well as observation, to appreciate it. - Frederick Douglass",
        "I believe that if you don't derive a deep sense of purpose from what you do, if you don't come radiantly alive several times a day, if you don't feel deeply grateful at the tremendous good fortune that has been bestowed on you, then you are wasting your life. And life is too short to waste. - Srikumar Rao",
        "Love doesn't make the world go 'round. Love is what makes the ride worthwhile. - Franklin P. Jones",
        "Being deeply loved by someone gives you strength, while loving someone deeply gives you courage. - Lao Tzu",
        "I have found the paradox, that if you love until it hurts, there can be no more hurt, only more love. - Mother Teresa",
        "A good leader takes a little more than his share of the blame, a little less than his share of the credit. - Arnold H. Glasow",
        "Hold yourself responsible for a higher standard than anybody expects of you. Never excuse yourself. - Henry Ward Beecher",
        "Empty your mind, be formless. Shapeless like water. If you put water into a cup, it becomes the cup. You put water into a bottle it becomes the bottle. You put it in a teapot, it becomes the teapot. Now, water can flow or it can crash. Be water, my friend. - Bruce Lee",
        "The things you own end up owning you. - Tyler Durden",
        "Attract what you expect, reflect what you desire, become what you respect, mirror what you admire.",
        "Life is but a dream to me. Gunshots sing to these other guys but lullabies don’t mean a thing to me. - Jay-Z",
        " God's a seamstress that tailor fitted my pain. - 50 cent",
        "The plan was to drink until the pain over. But what’s worse the pain or the hangover? - Kanye West",
        "First ship em dope and let em deal to brothers. Give em guns, step back and watch em kill each other. It’s time to fight back that what Huey said. Two shots in the dark, now Huey’s dead. - 2pac",
        "Just know I chose my own fate. I drove by the fork in the road and went straight. - Jay-Z",
        "The purpose of life is a life with a purpose so I rather die for a cause then live a life that is worthless.",
        "We walk the same path but got on different shoes. Live in the same building but we got different views. - Drake",
        "Wanna move out the hood and defeat that cancer. I ask how she stay on her feet like dancers. How she keep adding paint to a life size canvas. - Lil B",
        "There are few monsters who warrant the fear we have of them. - Andre Gide",
        "The difference between stupidity and genius is that genius has its limits. - Albert Einstein",
        "Talent hits a target no one else can hit. Genius hits a target no one else can see. - Arthur Schopenhauer",
        "Intelligence without ambition is a bird without wings. - Salvador Dali",
        "An intellectual is someone whose mind watches itself. - Albert Camus",
        "I tell myself that God gave my children\nmany gifts. Spirit, beauty, intelligence,\nthe capacity to make friends and to\ninspire respect. There was only one gift\nhe held back. – length of life\n- Rose Kennedy",
        "If everyone is thinking alike then somebody isn’t thinking. - George Patton",
        "I will love the light for it shows me the way, yet I will endure the darkness because it shows me the stars. - Og Man Dino",
        "Not to spoil the ending for you, but everything is going to be okay.",
        "It has been said that time heals all wounds. I don't agree. The wounds remain. Time - the mind, protecting its sanity - covers them with some scar tissue and the pain lessens, but it is never gone. - Rose Kennedy",
        "I'm for truth, no matter who tells it. I'm for justice, no matter who it's for or against. - Malcolm X",
        "God is inside of us. We can create any universe we want to live in. -God",
        "Girls shouldn't be hit, not even with a flower... not even with a bouquet of flowers.",
        "Man is still the most extraordinary computer of them all. - John F. Kennedy",
        "If a free society cannot help the many who are poor, it cannot save the few who are rich. - John F. Kennedy",
        "Geography has made us neighbors. History has made us friends. Economics has made us partners, and necessity has made us allies. Those whom God has so joined together, let no man put asunder. - John F. Kennedy",
        "I look forward to a great future for America - a future in which our country will match its military strength with our moral restraint, its wealth with our wisdom, its power with our purpose. - John F. Kennedy",
        "Mankind must put an end to war before war puts an end to mankind. - John F. Kennedy",
        "Forgive your enemies, but never forget their names. - John F. Kennedy",
        "If art is to nourish the roots of our culture, society must set the artist free to follow his vision wherever it takes him. - John F Kennedy",
        "The very word 'secrecy' is repugnant in a free and open society; and we are as a people inherently and historically opposed to secret societies, to secret oaths, and to secret proceedings. - John F. Kennedy",
        "When I die, I want to go peacefully like my grandfather did–in his sleep. Not yelling and screaming like the passengers in his car. - Bob Monkhouse",
        "NEVER FEAR\n\nPoverty * Criticism * Ill Health * Loss of Someone's Love * Old Age * Death\n\nEMBRACE\n\nDesire * Faith * Love * Sex * Enthusiasm * Romance * Hope\n\nAVOID\n\nFear * Hatred * Jealousy * Revenge * Superstition * Anger * Greed",
        "If you want to find the secrets of the universe, think in terms of energy, frequency and vibration. - Nikola Tesla",
        "There comes a time where silence is betrayal - MLK",
        "You are witnessing elegance in the form of a black elephant\nSmoking white rhino on terraces\nWill I die slain like my king by a terrorist?\nWill my woman be Coretta, take my name and cherish it?\nOr will she Jackie O, drop the Kennedy, remarry it?\nMy sister say it's necessary on some Cleopatra shit – Killer Mike",
        "I don't trust the church or the government\n Democrat, Republican\nPope or a bishop or them other men - Killer Mike",
        "Don't ever go with the flow be the flow - Jay-Z",
        "It is no measure of health to be well adjusted to a profoundly sick society. - Jiddu Krishnamurti",
        "The constant assertion of belief is an indication of fear. - Jiddu Krishnamurti",
        "If we can really understand the problem, the answer will come out of it, because the answer is not separate from the problem. ― Jiddu Krishnamurti",
        "You must understand the whole of life, not just one little part of it. That is why you must read, that is why you must look at the skies, that is why you must sing, and dance, and write poems, and suffer, and understand, for all that is life. ― Jiddu Krishnamurti",
        "Tell your friend that in his death, a part of you dies and goes with him. Wherever he goes, you also go. He will not be alone. ― Jiddu Krishnamurti",
        "The more you know yourself, the more clarity there is. Self-knowledge has no end - you don't come to an achievement; you don't come to a conclusion. It is an endless river. ― Jiddu Krishnamurti",
        "Forgive them father they know not what they do - Jesus Christ",
        "The most important decision we make is whether we believe we live in a friendly or hostile universe. - Albert Einstein",
        "If it doesn't open it’s not your door.",
        "There are things I know\nThere are things I don't know\nThere are things I don t know I don't know",
        "When there is solidarity and unity among the oppressed, the oppressor becomes a prey! ",
        "Habits begin as cobwebs and end as chains.",
        "To have once been a criminal is no disgrace. To remain a criminal is the disgrace. - Malcolm X",
        "History is a people's memory, and without a memory, man is demoted to the lower animals. - Malcolm X")

    val deepQueue = arrayListOf(
        "There's gonna be a lot of slow singing and flower bringing if my burglar alarm starts ringing. - Notorious",
        "When I get involved, I give it my heart. I mean my mind, my soul, my body. I mean every part. But if it doesn’t work out, it just doesn’t if it wasn’t meant to be you know it just wasn’t.",
        "I got mouths to feed. Unnecessary beef is more cows to breed.",
        "Just know I chose my own fate. I drove by the fork in the road and went straight. - Jay-Z",
        "Even if they won’t let me in Heaven I’ll raise Hell, til its Heaven. - Jay-Z",
        "There are few things that’s forever, my lady. We can make war or make babies. - Method Man",
        "Life is but a dream to me. Gunshots sing to these other guys but lullabies don’t mean a thing to me. - Jay-Z",
        "I don’t understand the difficult people. Love your brother treat him as your equal.",
        "The purpose of life is a life with a purpose So I rather die for a cause then live a life that is worthless.",
        "I rather die enormous than live dormant. - Jay-Z",
        "We can’t complain for this borrowed time. So don’t misuse yours cause you can’t borrow mine.",
        "We all seem to stumble planning our own demise. Getting the big picture and making it wallet sized.",
        "I keep my enemies close. I give em enough rope. They put themselves in the air. I just kick away the chair. - Jay-Z",
        "I’m trying to beat life cause I can’t cheat death.",
        "Lead by example don’t get caught up in the rapture. Life is just a raffle, mostly pain but some laughter. - Big Krit",
        "We walk the same path but got on different shoes. Live in the same building but we got different views. - Drake",
        "A simple right or left can mean life or death. Epic fail or nice success. Days of pleasure or nights of stress. - Jay-Z",
        "First ship em dope and let em deal to brothers. Give em guns, step back and watch em kill each other. It’s time to fight back that what Huey said. Two shots in the dark, now Huey’s dead. - 2pac",
        "I aint a killer but don’t push me. Revenge is like the sweetest joy next to getting pussy. - 2pac",
        "Real G’s move in silence like lasagna. - Lil Wayne",//20

        "Wanna move out the hood and defeat that cancer. I ask how she stay on her feet like dancers. How she keep adding paint to a life size canvas. - Lil B",
        "Thinking of a master plan cause aint nothin but sweat inside my hand. - Rakim",
        "There are few monsters who warrant the fear we have of them. - Andre Gide",
        "The difference between stupidity and genius is that genius has its limits. - Albert Einstein",
        "Talent hits a target no one else can hit. Genius hits a target no one else can see.",
        "Intelligence without ambition is a bird without wings. - Salvador Dali",
        "An intellectual is someone whose mind watches itself. - Albert Camus",
        "I tell myself that God gave my children\nmany gifts. Spirit, beauty, intelligence,\nthe capacity to make friends and to\ninspire respect. There was only one gift\nhe held back. – length of life\n- Rose Kennedy",
        "If everyone is thinking alike then somebody isn’t thinking. - George Patton",
        "I don’t even call it violence when it’s in self-defense. I call it intelligence. - Malcolm X",//30
        "Nothing is impossible, the word itself says “I’m possible”. - Audrey Hepburn",
        "To the mind that is still the whole universe surrenders. - Lao Tzu",
        "What lies behind you and what lies in front of you pales in comparison to what lies inside of you. - Ralph Waldo Emerson",
        " I will love the light for it shows me the way, yet I will endure the darkness because it shows me the stars. - Og Man Dino",
        "Not to spoil the ending for you, but everything is going to be okay.",
        "When plunder becomes a way of life for a group of men living together in society, they create for themselves in the course of time a legal system that authorizes it and a moral code that glorifies it. - Frederic Bastiat",
        "Empty your mind, be formless. Shapeless like water. If you put water into a cup, it becomes the cup. You put water into a bottle it becomes the bottle. You put it in a teapot, it becomes the teapot. Now, water can flow or it can crash. Be water, my friend. - Bruce Lee",
        "The things you own end up owning you. - Tyler Durden",
        "Attract what you expect, reflect what you desire, become what you respect, mirror what you admire.",
        "Dead in the middle of Little Italy little did we know that we riddled some middlemen who didn't do diddly. – Big Pun",//40
        "Sick, sick dreams of picnic scenes. Two kids, 16 with M-16’s and 10 clips each. And them shits reach through six kids each. And Slim gets blamed in Bill Clints speech to fix these streets. - Eminem",
        "I'm for truth, no matter who tells it. I'm for justice, no matter who it's for or against. - Malcolm X",
        "Nobody can give you freedom. Nobody can give you equality or justice or anything. If you're a man, you take it. - Malcolm X",
        "I believe in the brotherhood of man, all men, but I don't believe in brotherhood with anybody who doesn't want brotherhood with me. I believe in treating people right, but I'm not going to waste my time trying to treat somebody right who doesn't know how to return the treatment. - Malcolm X",
        "God is inside of us. We can create any universe we want to live in. - God",
        "Girls shouldn't be hit, not even with a flower... not even with a bouquet of flowers.",
        "Man is still the most extraordinary computer of them all. - John F. Kennedy",
        "If a free society cannot help the many who are poor, it cannot save the few who are rich. - John F. Kennedy",
        "Geography has made us neighbors. History has made us friends. Economics has made us partners, and necessity has made us allies. Those whom God has so joined together, let no man put asunder. - John F. Kennedy",
        "I look forward to a great future for America - a future in which our country will match its military strength with our moral restraint, its wealth with our wisdom, its power with our purpose. - John F. Kennedy",//50
        "Mankind must put an end to war before war puts an end to mankind. - John F. Kennedy",
        "Forgive your enemies, but never forget their names. - John F. Kennedy",
        "If art is to nourish the roots of our culture, society must set the artist free to follow his vision wherever it takes him. - John F Kennedy",
        "The very word 'secrecy' is repugnant in a free and open society; and we are as a people inherently and historically opposed to secret societies, to secret oaths, and to secret proceedings. - John F. Kennedy",
        "When I die, I want to go peacefully like my grandfather did–in his sleep. Not yelling and screaming like the passengers in his car. - Bob Monkhouse",
        "NEVER FEAR\n\nPoverty * Criticism * Ill Health * Loss of Someone's Love * Old Age * Death\n\nEMBRACE\n\nDesire * Faith * Love * Sex * Enthusiasm * Romance * Hope\n\nAVOID\n\nFear * Hatred * Jealousy * Revenge * Supterstition * Anger * Greed",
        "If you want to find the secrets of the universe, think in terms of energy, frequency and vibration. - Nikola Tesla",
        "Blame Reagan for turning me into monster.\nBlame Oliver North and Iran-Contra.\nI ran contraband that they sponsored.\nBefore this rhymin’ stuff we was in concert. Jay-Z",
        "There comes a time where silence is betrayal - MLK ",
        "You are witnessing elegance in the form of a black elephant\nSmoking white rhino on terraces\nWill I die slain like my king by a terrorist?\nWill my woman be Coretta, take my name and cherish it?\n Or will she Jackie O, drop the Kennedy, remarry it?\nMy sister say it's necessary on some Cleopatra shit – Killer Mike",//60
        "I don't trust the church or the government\nDemocrat, Republican\nPope or a bishop or them other men - Killer Mike",
        "If you die on me, Bitch I'll kill you - Ike Turner",
        "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me.",
        "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me. Thou preparest a table before me in the presence of mine enemies: thou anointest my head with oil; my cup runneth over. Surely goodness and mercy shall follow me all the days of my life: and I will dwell in the house of the LORD forever.",
        "My ceiling’s absent, my wheels are massive, my friend’s assassins\nAll of us bastards, our mothers’ queens and our women dancers - Lil Wayne",
        "The loudest one in the room is the weakest one in the room - Frank Lucas",
        "Don't ever go with the flow be the flow - Jay-Z",
        "It is no measure of health to be well adjusted to a profoundly sick society. - Jiddu Krishnamurti",
        "The constant assertion of belief is an indication of fear. - Jiddu Krishnamurti",
        "If we can really understand the problem, the answer will come out of it, because the answer is not separate from the problem. ― Jiddu Krishnamurti",//70
        "You must understand the whole of life, not just one little part of it. That is why you must read, that is why you must look at the skies, that is why you must sing, and dance, and write poems, and suffer, and understand, for all that is life. ― Jiddu Krishnamurti",
        "Tell your friend that in his death, a part of you dies and goes with him. Wherever he goes, you also go. He will not be alone. ― Jiddu Krishnamurti",
        "The more you know yourself, the more clarity there is. Self-knowledge has no end - you don't come to an achievement; you don't come to a conclusion. It is an endless river. ― Jiddu Krishnamurti",
        "Forgive them father they know not what they do - Jesus Christ",
        "If it doesn't open it’s not your door.",
        "The most important decision we make is whether we believe we live in a friendly or hostile universe. - Albert Einstein",
        "When there is solidarity and unity among the oppressed, the oppressor becomes a prey! ",
        "I also don't believe in drugs. For years I paid my people extra so they wouldn't do that kind of business. Somebody comes to them and says, I have powders; if you put up three, four thousand dollar investment - we can make fifty thousand distributing. So they can't resist. I want to control it as a business, to keep it respectable. I don't want it near schools - I don't want it sold to children! That's an infamia. In my city, we would keep the traffic in the dark people - the colored. They're animals anyway, so let them lose their souls. ― Giuseppe Zaluchi",
        "I don't hustle for my first name I hustle for my last - Dame Dash",
        "Jealousy is a weak emotion.",//80
        "It's like: my cousin died, granny died, Mani died; ain't quit\nLaid my head down at night, gunfire; can't quit\nPoverty across the globe, identical, same shit\nBut if it ain't affecting us, we acting like it ain't shit- Jon Connor",
        "I ain't sorry for not giving up\nLook where I ended up\nBut being humble for so long\nPushed me to the point that I don't give a fuck - Jon Connor",
        "Habits begin as cobwebs and end as chains.",
        "To have once been a criminal is no disgrace. To remain a criminal is the disgrace. - Malcolm X",
        "History is a people's memory, and without a memory, man is demoted to the lower animals. - Malcolm X",
        "Power doesn't back up in the face of a smile, or in the face of a threat of some kind of nonviolent loving action. It's not the nature of power to back up in the face of anything but some more power. - Malcolm X ",
        "Ghettos, America. U.S. to the izzay\n Killa in the citywide sprizzay\nWhere there's sunshine in the shizzade\nChurch won't pull him out the pin like a grenade\nFor acting out their fears like a charade - Lupe Fiasco",
        "God forgive me for my brash delivery\nBut I remember vividly what these streets did to me\nSo picture me letting these clowns’ nitpick at me\nPaint me like a pickiny - Jay Z"
    )
}